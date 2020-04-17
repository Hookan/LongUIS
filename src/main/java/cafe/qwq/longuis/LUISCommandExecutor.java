package cafe.qwq.longuis;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LUISCommandExecutor implements CommandExecutor
{
    private JsonParser jsonParser = new JsonParser();
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender.hasPermission("lui.use"))
        {
            if (args.length != 2) sender.sendMessage("Wrong arguments.");
            Player player = Bukkit.getPlayer(args[0]);
            JsonElement element = jsonParser.parse(args[1]);
            LongUIS.openGui(player, element);
        }
        else sender.sendMessage("Permission Denied.");
        return true;
    }
}
