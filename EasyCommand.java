import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class EasyCommand implements CommandExecutor {

    /**
     * @author: Nonopichy
     * @discord: Nonopichy#1373 / Nono#2009 (@deprecated)
     */
    
    /**
    * Usage, delete this 'MainPlugin' please.
    */

    class MainPlugin extends JavaPlugin {
        public void onEnable(){
            new EasyCommand(this, "commad", false, true, 0, new String[]{"cmd","cmd_"}, new EasyCommandInterface() {
                @Override
                public void onCommand(CommandSender commandSender, String s, String[] args) {
                    commandSender.sendMessage("Command!");
                }
                @Override
                public void onCooldown(Player p, long cooldown) {
                    p.sendMessage("In cooldown, wait seconds: "+cooldown);
                }
            });
        }
    }

    private Plugin plugin;
    private String commandName = null;
    private String commandAliases = "";
    private Boolean consoleUsage;
    private Boolean playerUsage;
    private int cooldown = 0;
    private EasyCommandInterface easyCommandInterface;

    public String getCommandName(){
        return commandName;
    }
    public boolean getConsoleUsage(){
        return consoleUsage;
    }
    public boolean getPlayerUsage(){
        return playerUsage;
    }
    public void setPlayerUsage(boolean playerUsage){
        this.playerUsage = playerUsage;
    }
    public void setConsoleUsage(boolean consoleUsage){
        this.consoleUsage = consoleUsage;
    }
    public void setCooldown(int cooldown){
        this.cooldown = cooldown;
    }
    public int getCooldown(){
        return this.cooldown;
    }
    public void setCommandAliases(String[] aliases){
        for(int i  = 0; i < aliases.length ; i++) {
            if(i > aliases.length - 1) commandAliases = commandName + aliases[i];
            else commandAliases = commandName + aliases[i] + ",";
        }
    }
    public String getCommandAliases(){
        return this.commandAliases;
    }

    public void setEasyCommandInterface(EasyCommandInterface easyCommandInterface){
        this.easyCommandInterface = easyCommandInterface;
    }

    private Boolean isAliases(String str){
        return commandAliases.contains(str.toLowerCase());
    }
    private HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    private long inCooldown(Player p, int cooldown){
        if(cooldown == 0) return 0;
        if(cooldowns.containsKey(p.getName())) {
            long l = ((cooldowns.get(p.getName())/1000)+cooldown) - (System.currentTimeMillis()/1000);
            if(l>0)
                return l;
        }
        cooldowns.put(p.getName(), System.currentTimeMillis());
        return 0;
    }

    public EasyCommand(Plugin plugin, String commandName, boolean consoleUsage, boolean playerUsage, int cooldown, String[] aliases, EasyCommandInterface easyCommandInterface){
        this.plugin = plugin;
        this.commandName = commandName;
        this.consoleUsage = consoleUsage;
        this.playerUsage = playerUsage;
        this.cooldown = cooldown;
        if(aliases!=null) setCommandAliases(aliases);
        this.plugin.getServer().getPluginCommand(commandName).setExecutor(this);

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase(commandName) || isAliases(command.getName())){
            if(commandSender instanceof Player){
                if(!playerUsage)
                    return false;
                long l = inCooldown(((Player) commandSender).getPlayer(), cooldown);
                if(l>0)
                    easyCommandInterface.onCooldown(((Player) commandSender).getPlayer(),l);
            } else { if(!consoleUsage) return false; }
            easyCommandInterface.onCommand(commandSender,s,args);
            return true;
        }
        return false;
    }

    public interface EasyCommandInterface {
        void onCommand(CommandSender commandSender,  String s, String[] args);
        void onCooldown(Player p, long cooldown);
    }

}
