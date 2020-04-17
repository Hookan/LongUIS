package cafe.qwq.longuis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class LongUIS extends JavaPlugin implements Listener
{
    private static final String channel = "longui:main";
    private static LongUIS instance;
    private Logger logger;
    
    public LongUIS()
    {
        super();
        instance = this;
    }
    
    public void onEnable()
    {
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, channel, LongUIS::onReceivePacket);
        messenger.registerOutgoingPluginChannel(this, channel);
        
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("luiopengui").setExecutor(new LUISCommandExecutor());
        
        logger = Bukkit.getLogger();
        logger.info(getName() + " has been loaded !");
    }
    
    public void onDisable()
    {
        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this, channel);
        messenger.unregisterOutgoingPluginChannel(this, channel);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        try
        {
            Class clazz = player.getClass();
            Method addChannel = clazz.getDeclaredMethod("addChannel", String.class);
            addChannel.setAccessible(true);
            addChannel.invoke(player, channel);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 在客户端打开一个LongUI GUI界面
     *
     * @param player 玩家实例
     * @param gui    GUI的json设置，格式与LongUI的GUI json格式相同
     */
    public static void openGui(Player player, JsonElement gui)
    {
        JsonObject obj = new JsonObject();
        obj.add("value", gui);
        obj.addProperty("type", "og");//open gui
        send(player, obj.toString());
    }
    
    /**
     * 向客户端的Long UI发包并调用客户端的js回调函数
     * 客户端的js回调函数需要在客户端的js函数luiScreenInit中使用addPacketReceiver来注册
     *
     * @param player 玩家实例
     * @param plugin 发包的插件实例
     * @param value  自定义的一个json，格式任意
     */
    public static void sendPacket(Player player, Plugin plugin, JsonElement value)
    {
        JsonObject obj = new JsonObject();
        obj.add("value", value);
        obj.addProperty("type", "np");//normal packet
        obj.addProperty("plugin", plugin.getName());
        send(player, obj.toString());
    }
    
    private static Map<String, IPacketReceiver> receiverMap = new HashMap<>();
    
    /**
     * 注册接受包的回调函数
     * 请注意，回调函数调用的线程并非主线程
     * 在回调函数内请注意线程安全
     * 可以使用 Bukkit.getScheduler().runTask(Plugin ,Runnable ) 来添加主线程任务
     *
     * @param plugin   插件实例
     * @param receiver 接受包的回调函数
     */
    public static void registerPacketReceiver(Plugin plugin, IPacketReceiver receiver)
    {
        receiverMap.put(plugin.getName(), receiver);
    }
    
    /**
     * 注销接受包的回调函数
     *
     * @param plugin 插件实例
     */
    public static void unregisterPacketReceiver(Plugin plugin)
    {
        if (receiverMap.containsKey(plugin.getName()))
            receiverMap.remove(plugin.getName());
    }
    
    public interface IPacketReceiver
    {
        /**
         * 接受包的回调函数
         * 请注意该方法不在主线程执行！
         * 在回调函数内请注意线程安全
         * 可以使用 Bukkit.getScheduler().runTask(Plugin ,Runnable ) 来添加主线程任务
         *
         * @param element 客户端发的json，格式任意
         * @param player 客户端对应的玩家
         */
        void callback(JsonElement element, Player player);
    }
    
    private static String read(byte[] bytes)
    {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        if (buf.readByte() == 0)
        {
            return buf.toString(StandardCharsets.UTF_8);
        }
        return null;
    }
    
    private static void send(Player player, String msg)
    {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(bytes.length + 1);
        buf.writeByte(0);
        buf.writeBytes(bytes);
        player.sendPluginMessage(instance, channel, buf.array());
    }
    
    private static JsonParser jsonParser = new JsonParser();
    
    private static void onReceivePacket(String ch, Player player, byte[] bytes)
    {
        String message = read(bytes);
        if (message != null)
        {
            JsonObject obj = jsonParser.parse(message).getAsJsonObject();
            String plugin = obj.get("plugin").getAsString();
            if (receiverMap.containsKey(plugin))
            {
                IPacketReceiver receiver = receiverMap.get(plugin);
                receiver.callback(obj.get("value"), player);
            }
            else
            {
                instance.logger.warning("Can't find plugin " + plugin + " !");
            }
        }
    }
}

