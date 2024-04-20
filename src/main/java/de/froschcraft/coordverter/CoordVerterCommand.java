package de.froschcraft.coordverter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CoordVerterCommand implements CommandExecutor
{
    private final MiniMessage mm;
    private final FileConfiguration config;

    public CoordVerterCommand(@NotNull FileConfiguration config,
                              @NotNull MiniMessage mm)
    {
        // We don't want to duplicate any unnecessary objects.
        this.config = config;
        this.mm = mm;
    }

    private String getConfig(@NotNull String key, String formatOptions) {
        String config_element = Objects.requireNonNull(config.getString(key));

        if (Objects.isNull(formatOptions)) return config_element;
        else return String.format(formatOptions, config_element);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args)
    {

        // Block command line users from using the command.
        if (!(sender instanceof Player)) {
            Component command_user_error = mm.deserialize(getConfig("messages.command-user-error", null));
            sender.sendMessage(command_user_error);
            return true;
        }

        // Check for permissions
        if (!sender.hasPermission("coordverter.convert")) {
            Component missing_permission_error = mm.deserialize(getConfig("messages.missing-permission-error", null));
            sender.sendMessage(missing_permission_error);
            return true;
        }

        /*
         * Check whether the correct amount of arguments has been passed. We could return false and let
         * Bukkit handle the usage functionality but here we get some nice formatting.
         */
        if (args.length != 4) {
            Component argument_amount_error = mm.deserialize(getConfig("messages.argument-amount-error", null));
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
            Component invalid_format_error = mm.deserialize(getConfig("messages.invalid-format-error", null));
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

            Component unknown_dimension_error = mm.deserialize(getConfig("messages.invalid-format-error", null));
            sender.sendMessage(unknown_dimension_error);
            return true;

        }

        /*
         * Build table from string block. All right, why does it look so ugly? >.< Because BuildString introduced a bug with the amount
         * of whitespace characters shifting the output in Minecraft, this solution works.
         */
        String output = getConfig("messages.prefix-output", null) +
                getConfig("messages.overworld-output", null) +
                getConfig("messages.nether-output", null) +
                getConfig("messages.suffix-output", null);

        // Replace overworld coordinates.
        output = output.replace("${x_ow}", String.format("%12.0f", overworld_coords[0]))
        .replace("${y_ow}", String.format("%12.0f", overworld_coords[1]))
        .replace("${z_ow}", String.format("%12.0f", overworld_coords[2]))

        // Replace nether coordinates.
        .replace("${x_nt}", String.format("%12.0f", nether_coords[0]))
        .replace("${y_nt}", String.format("%12.0f", nether_coords[1]))
        .replace("${z_nt}", String.format("%12.0f", nether_coords[2]))

        // Replace world names.
        .replace("${overworld}", getConfig("messages.overworld-name","%-12s"))
        .replace("${nether}", getConfig("messages.nether-name","%-12s"));

        // Deserialize and output table to the user.
        Component table_deserialized = mm.deserialize(output);
        sender.sendMessage(table_deserialized);

        return true;
    }
}
