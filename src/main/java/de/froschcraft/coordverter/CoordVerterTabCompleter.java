package de.froschcraft.coordverter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CoordVerterTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args)
    {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            // Add all possible commands for the first parameter
            commands.add("overworld");
            commands.add("nether");
            // Copy all the matching commands ignoring casing.
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (2 <= args.length && 4 >= args.length) {
            commands.add("~");
            StringUtil.copyPartialMatches(args[args.length - 1], commands, completions);
        }

        return completions;
    }
}
