package de.froschcraft.coordverter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoordVerterCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args)
    {
        // For formatting Minecraft messages.
        @NotNull MiniMessage mm = MiniMessage.miniMessage();

        // Block command line users from using the command.
        if (!(sender instanceof Player)) {
            Component command_user_error = mm.deserialize("<dark_grey>This command can only be used by <underline>players</underline>.</dark_grey>");
            sender.sendMessage(command_user_error);
            return true;
        }

        // Check for permissions
        if (!sender.hasPermission("coordverter.convert")) {
            Component missing_permission_error = mm.deserialize("<dark_grey>You lack the rights to use this command. Womp womp</dark_grey>");
            sender.sendMessage(missing_permission_error);
            return true;
        }

        /*
         * Check whether the correct amount of arguments has been passed. We could return false and let
         * Bukkit handle the usage functionality but here we get some nice formatting.
         */
        if (args.length != 4) {
            Component argument_amount_error = mm.deserialize("<dark_grey>Usage: /convert <world> <x> <y> <z></dark_grey>");
            sender.sendMessage(argument_amount_error);
            return true;
        }

        // Get the dimension and initiate a variable which will later hold our coordinates.
        String dimension = args[0];
        double[] coordinates = new double[3];

        // Preemptively get player location if he used the tidal operator.
        Location player_location = ((Player) sender).getLocation();
        double[] player_coords = {player_location.getX(), player_location.getY(), player_location.getZ()};

        /*
         * Here we parse what's supposed to be our coordinates, if this fails we output an error. The player
         * may use the tidal operator in which case we take his coordinates.
         */
        try {

            for (int idx = 0; idx < 3; idx++) {
                coordinates[idx] = args[idx + 1].equals("~") ? player_coords[idx] : Double.parseDouble(args[idx + 1]);
            }

        } catch (NumberFormatException e) {
            Component invalid_format_error = mm.deserialize("<dark_grey>Invalid coordinate format.</dark_grey>");
            sender.sendMessage(invalid_format_error);
            return true;
        }

        /*
         * Okay, so the logic here is that depending on in which if-statement we end up one of those arrays will
         * save the converted coordinates and one will end up being a reference to the original coordinates array.
         * If the dimension is unknown throw an error.
         */
        double[] nether_coords;
        double[] overworld_coords;

        if (dimension.equalsIgnoreCase("overworld")) {
            overworld_coords = coordinates;
            nether_coords = new double[3];

            for (int idx = 0; idx < 3; idx++) {
                nether_coords[idx] = (coordinates[idx] / 8);
            }

        } else if (dimension.equalsIgnoreCase("nether")) {
            overworld_coords = new double[3];
            nether_coords = coordinates;

            for (int idx = 0; idx < 3; idx++) {
                overworld_coords[idx] = (coordinates[idx] * 8);
            }

        } else {

            Component argument_amount_error = mm.deserialize("<dark_grey>Unknown dimension. Options are:\n<aqua>Overworld</aqua>, <red>Nether</red></dark_grey>");
            sender.sendMessage(argument_amount_error);
            return true;

        }

        /*
         * Build table from string block. All right, why does it look so ugly? >.< Because BuildString introduced a bug with the amount
         * of whitespace characters shifting the output in Minecraft, this solution works.
         */
        String table = String.format("""
<newline><font:uniform><dark_grey><click:copy_to_clipboard:%.0f %.0f %.0f><hover:show_text:'Copy to Clipboard'><aqua>%-12s</aqua></hover></click>|%12.0f|%12.0f|%12.0f</dark_grey>
<dark_grey><click:copy_to_clipboard:%.0f %.0f %.0f><hover:show_text:'Copy to Clipboard'><red>%-12s</red></hover></click>|%12.0f|%12.0f|%12.0f</dark_grey></font><newline>""",
                overworld_coords[0], overworld_coords[1], overworld_coords[2],
                "Overworld", overworld_coords[0], overworld_coords[1], overworld_coords[2],
                nether_coords[0], nether_coords[1], nether_coords[2],
                "Nether", nether_coords[0], nether_coords[1], nether_coords[2]);

        // Deserialize and output table to the user.
        Component table_deserialized = mm.deserialize(table);
        sender.sendMessage(table_deserialized);

        return true;
    }
}
