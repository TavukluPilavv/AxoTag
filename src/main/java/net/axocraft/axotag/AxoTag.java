package net.axocraft.axotag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Map;

public final class AxoTag extends JavaPlugin implements Listener, TabCompleter {
    private LuckPerms luckPerms;
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    private final Map<String, NamedTextColor> colors = Map.ofEntries(
        Map.entry("siyah", NamedTextColor.BLACK), Map.entry("black", NamedTextColor.BLACK),
        Map.entry("kapalı mavi", NamedTextColor.DARK_BLUE), Map.entry("koyu mavi", NamedTextColor.DARK_BLUE), Map.entry("dark blue", NamedTextColor.DARK_BLUE), Map.entry("dark_blue", NamedTextColor.DARK_BLUE),
        Map.entry("kapalı yeşil", NamedTextColor.DARK_GREEN), Map.entry("koyu yeşil", NamedTextColor.DARK_GREEN), Map.entry("dark green", NamedTextColor.DARK_GREEN), Map.entry("dark_green", NamedTextColor.DARK_GREEN),
        Map.entry("kapalı turkuaz", NamedTextColor.DARK_AQUA), Map.entry("koyu aqua", NamedTextColor.DARK_AQUA), Map.entry("dark aqua", NamedTextColor.DARK_AQUA), Map.entry("dark_aqua", NamedTextColor.DARK_AQUA),
        Map.entry("kapalı kırmızı", NamedTextColor.DARK_RED), Map.entry("koyu kırmızı", NamedTextColor.DARK_RED), Map.entry("dark red", NamedTextColor.DARK_RED), Map.entry("dark_red", NamedTextColor.DARK_RED),
        Map.entry("kapalı mor", NamedTextColor.DARK_PURPLE), Map.entry("koyu mor", NamedTextColor.DARK_PURPLE), Map.entry("dark purple", NamedTextColor.DARK_PURPLE), Map.entry("dark_purple", NamedTextColor.DARK_PURPLE),
        Map.entry("turuncu", NamedTextColor.GOLD), Map.entry("altın", NamedTextColor.GOLD), Map.entry("gold", NamedTextColor.GOLD),
        Map.entry("gri", NamedTextColor.GRAY), Map.entry("gray", NamedTextColor.GRAY), Map.entry("grey", NamedTextColor.GRAY),
        Map.entry("kapalı gri", NamedTextColor.DARK_GRAY), Map.entry("koyu gri", NamedTextColor.DARK_GRAY), Map.entry("dark gray", NamedTextColor.DARK_GRAY), Map.entry("dark_gray", NamedTextColor.DARK_GRAY),
        Map.entry("mavi", NamedTextColor.BLUE), Map.entry("blue", NamedTextColor.BLUE), Map.entry("parlak mavi", NamedTextColor.BLUE),
        Map.entry("yeşil", NamedTextColor.GREEN), Map.entry("green", NamedTextColor.GREEN), Map.entry("parlak yeşil", NamedTextColor.GREEN),
        Map.entry("aqua", NamedTextColor.AQUA), Map.entry("turkuaz", NamedTextColor.AQUA), Map.entry("açık mavi", NamedTextColor.AQUA), Map.entry("light blue", NamedTextColor.AQUA),
        Map.entry("kırmızı", NamedTextColor.RED), Map.entry("red", NamedTextColor.RED), Map.entry("parlak kırmızı", NamedTextColor.RED),
        Map.entry("mor", NamedTextColor.LIGHT_PURPLE), Map.entry("pembe", NamedTextColor.LIGHT_PURPLE), Map.entry("açık kırmızı", NamedTextColor.LIGHT_PURPLE), Map.entry("light red", NamedTextColor.LIGHT_PURPLE), Map.entry("açık mor", NamedTextColor.LIGHT_PURPLE),
        Map.entry("sarı", NamedTextColor.YELLOW), Map.entry("yellow", NamedTextColor.YELLOW), Map.entry("parlak sarı", NamedTextColor.YELLOW),
        Map.entry("beyaz", NamedTextColor.WHITE), Map.entry("white", NamedTextColor.WHITE),
        Map.entry("açık turuncu", NamedTextColor.GOLD), Map.entry("light orange", NamedTextColor.GOLD),
        Map.entry("açık yeşil", NamedTextColor.GREEN), Map.entry("light green", NamedTextColor.GREEN),
        Map.entry("açık gri", NamedTextColor.GRAY), Map.entry("light gray", NamedTextColor.GRAY)
    );

    @Override public void onEnable() {
        saveDefaultConfig();
        luckPerms = LuckPermsProvider.get();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("tagkomut").setTabCompleter(this);
        getLogger().info("AxoTag enabled.");
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("axotag.admin")) {
            sender.sendMessage(Component.text("Bu komutu kullanma yetkin yok.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("/tagkomut <grup> renk <renk> | chatrenk <renk>", NamedTextColor.YELLOW));
            return true;
        }
        String groupName = args[0];
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if (group == null) {
            group = luckPerms.getGroupManager().getLoadedGroups().stream()
                    .filter(g -> g.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
        }
        if (group == null) {
            sender.sendMessage(Component.text("LuckPerms grubu bulunamadı: " + groupName, NamedTextColor.RED));
            return true;
        }
        String action = args[1].toLowerCase(Locale.ROOT);
        if (action.equals("inceparlak")) {
            boolean enabled = args[2].equalsIgnoreCase("aç") || args[2].equalsIgnoreCase("ac") || args[2].equalsIgnoreCase("on");
            if (!enabled) {
                sender.sendMessage(Component.text("Kullanım: /tagkomut " + groupName + " inceparlak aç", NamedTextColor.YELLOW));
                return true;
            }
            NamedTextColor color = NamedTextColor.AQUA;
            String prefix = legacy.serialize(Component.text("[" + group.getName() + "] ", color))
                    + legacy.serialize(Component.text("", color));
            setPrefix(group, prefix);
            sender.sendMessage(Component.text(group.getName() + " tagı ince/parlak (aqua) yapıldı.", NamedTextColor.GREEN));
            return true;
        }
        if (action.equals("renk")) {
            String colorName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)).toLowerCase(Locale.ROOT);
            NamedTextColor color = colors.get(colorName);
            if (color == null) {
                sender.sendMessage(Component.text("Renk bulunamadı. Örnek: mavi, kırmızı, mor, yeşil, sarı, aqua, beyaz.", NamedTextColor.RED));
                return true;
            }
            String prefix = legacy.serialize(Component.text("[" + group.getName() + "] ", color));
            setPrefix(group, prefix);
            sender.sendMessage(Component.text(group.getName() + " tag rengi ayarlandı.", NamedTextColor.GREEN));
            return true;
        }
        if (action.equals("chatrenk")) {
            if (args.length < 3) {
                sender.sendMessage(Component.text("Kullanım: /tagkomut " + groupName + " chatrenk <renk> | chatrenk kapat", NamedTextColor.YELLOW));
                return true;
            }
            String colorName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)).toLowerCase(Locale.ROOT);
            if (colorName.equals("kapat") || colorName.equals("off")) {
                getConfig().set("chat-colors." + group.getName(), null);
                saveConfig();
                sender.sendMessage(Component.text(group.getName() + " sohbet rengi kapatıldı.", NamedTextColor.GREEN));
                return true;
            }
            NamedTextColor color = colors.get(colorName);
            if (color == null) {
                sender.sendMessage(Component.text("Renk bulunamadı. Türkçe renk adlarını kullanabilirsin: kırmızı, mavi, turuncu, açık mavi, kapalı mavi, açık kırmızı, kapalı kırmızı, mor, yeşil, sarı, beyaz, siyah, gri.", NamedTextColor.RED));
                return true;
            }
            getConfig().set("chat-colors." + group.getName(), color.toString());
            saveConfig();
            sender.sendMessage(Component.text(group.getName() + " sohbet rengi " + colorName + " yapıldı.", NamedTextColor.GREEN));
            return true;
        }
        sender.sendMessage(Component.text("Bilinmeyen işlem. inceparlak, renk veya chatrenk kullan.", NamedTextColor.RED));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("axotag.admin")) return java.util.Collections.emptyList();
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        if (args.length == 1) {
            for (Group group : luckPerms.getGroupManager().getLoadedGroups()) suggestions.add(group.getName());
        } else if (args.length == 2) {
            suggestions.add("inceparlak");
            suggestions.add("renk");
            suggestions.add("chatrenk");
        } else if (args.length == 3 && args[1].equalsIgnoreCase("inceparlak")) {
            suggestions.add("aç");
        } else if (args.length >= 3 && (args[1].equalsIgnoreCase("renk") || args[1].equalsIgnoreCase("chatrenk"))) {
            suggestions.addAll(colors.keySet());
            if (args[1].equalsIgnoreCase("chatrenk")) suggestions.add("kapat");
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], suggestions, new java.util.ArrayList<>());
    }

    private void setPrefix(Group group, String prefix) {
        group.data().clear(node -> node instanceof MetaNode meta && meta.getMetaKey().equals("prefix"));
        group.data().add(MetaNode.builder("prefix", prefix, 100).build());
        luckPerms.getGroupManager().saveGroup(group);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        var user = luckPerms.getUserManager().getUser(event.getPlayer().getUniqueId());
        if (user == null) return;
        String primaryGroup = user.getPrimaryGroup();
        FileConfiguration config = getConfig();
        String stored = config.getString("chat-colors." + primaryGroup);
        if (stored == null) return;
        NamedTextColor color = NamedTextColor.NAMES.value(stored);
        if (color != null) event.message(event.message().color(color));
    }
}
