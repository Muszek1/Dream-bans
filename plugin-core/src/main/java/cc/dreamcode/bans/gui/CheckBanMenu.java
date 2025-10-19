package cc.dreamcode.bans.gui;

import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.Profile;
import cc.dreamcode.bans.punishments.Ban;
import cc.dreamcode.bans.punishments.IpBan;
import cc.dreamcode.bans.punishments.Kick;
import cc.dreamcode.bans.punishments.Mute;
import cc.dreamcode.bans.punishments.TempBan;
import cc.dreamcode.bans.punishments.TempMute;
import cc.dreamcode.menu.bukkit.BukkitMenuBuilder;
import cc.dreamcode.menu.bukkit.base.BukkitMenu;
import cc.dreamcode.menu.bukkit.base.BukkitMenuPaginated;
import cc.dreamcode.utilities.builder.MapBuilder;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import eu.okaeri.injector.annotation.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CheckBanMenu {

  private final PluginConfig pluginConfig;

  public void open(Profile profile, Player viewer) {
    BukkitMenuBuilder builder = this.pluginConfig.checkBanMenu;

    BukkitMenu baseMenu = builder.buildEmpty(
        new MapBuilder<String, Object>().put("name", safe(profile.getName())).build());

    baseMenu.setItem(4,
        makeItem(Material.PAPER, "§ePodsumowanie kar", "§7Bany: §c" + profile.getBans().size(),
            "§7TempBany: §c" + profile.getTempBans().size(),
            "§7Mute: §c" + profile.getMutes().size(),
            "§7TempMute: §c" + profile.getTempMutes().size(),
            "§7BanIP: §c" + profile.getIpBansCheck().size(),
            "§7Kicki: §c" + profile.getKicks().size()));

    BukkitMenuPaginated paginatedMenu = baseMenu.toPaginated();
    paginatedMenu.setPreviousPageSlot(48,
        new ItemBuilder(Material.ARROW).setName("&aPoprzednia strona").toItemStack(), e -> {
        });

    paginatedMenu.setNextPageSlot(50,
        new ItemBuilder(Material.ARROW).setName("&aNastępna strona").toItemStack(), e -> {
        });

    List<Integer> storageSlots = paginatedMenu.getStorageItemSlots();

    List<Integer> slotsToRemove = new ArrayList<>();
    for (int i = 0; i <= 8; i++) {
      slotsToRemove.add(i);
    }
    for (int i = 45; i <= 53; i++) {
      slotsToRemove.add(i);
    }
    storageSlots.removeAll(slotsToRemove);

    List<Object> allPunishments = new ArrayList<>();
    allPunishments.addAll(profile.getBans());
    allPunishments.addAll(profile.getTempBans());
    allPunishments.addAll(profile.getMutes());
    allPunishments.addAll(profile.getTempMutes());
    allPunishments.addAll(profile.getIpBansCheck());
    allPunishments.addAll(profile.getKicks());

    List<ItemStack> punishmentItems = allPunishments.stream().map(this::createPunishmentItem)
        .collect(Collectors.toList());

    paginatedMenu.addStorageItems(punishmentItems);

    paginatedMenu.openFirstPage(viewer);
  }

  private ItemStack createPunishmentItem(Object punishment) {
    if (punishment instanceof Ban) {
      Ban ban = (Ban) punishment;
      return makeItem(Material.BOOK, "§cBan", "§7Powód: §f" + safe(ban.getReason()),
          "§7Przez: §f" + safe(ban.getBannedBy()), "§7Data: §f" + formatDate(ban.getDate()),
          "§7Do: §f" + (ban.getUntil() > 0 ? formatDate(ban.getUntil()) : "§cPermanentny"));
    }
    if (punishment instanceof TempBan) {
      TempBan tempBan = (TempBan) punishment;
      return makeItem(Material.ENCHANTED_BOOK, "§cTempBan",
          "§7Powód: §f" + safe(tempBan.getReason()), "§7Przez: §f" + safe(tempBan.getBannedBy()),
          "§7Od: §f" + formatDate(tempBan.getDate()), "§7Do: §f" + formatDate(tempBan.getUntil()));
    }
    if (punishment instanceof Mute) {
      Mute mute = (Mute) punishment;
      return makeItem(Material.PAPER, "§7Mute", "§7Powód: §f" + safe(mute.getReason()),
          "§7Przez: §f" + safe(mute.getMutedBy()), "§7Data: §f" + formatDate(mute.getDate()),
          "§7Do: §f§cPermanentny");
    }
    if (punishment instanceof TempMute) {
      TempMute tempMute = (TempMute) punishment;
      return makeItem(Material.WRITABLE_BOOK, "§7TempMute",
          "§7Powód: §f" + safe(tempMute.getReason()), "§7Przez: §f" + safe(tempMute.getMutedBy()),
          "§7Od: §f" + formatDate(tempMute.getDate()),
          "§7Do: §f" + formatDate(tempMute.getUntil()));
    }
    if (punishment instanceof IpBan) {
      IpBan ipBan = (IpBan) punishment;
      return makeItem(Material.BEDROCK, "§4BanIP", "§7Powód: §f" + safe(ipBan.getReason()),
          "§7Przez: §f" + safe(ipBan.getBannedBy()), "§7Data: §f" + formatDate(ipBan.getDate()),
          "§7Do: §f" + (ipBan.getUntil() > 0 ? formatDate(ipBan.getUntil()) : "§cPermanentny"));
    }
    if (punishment instanceof Kick) {
      Kick kick = (Kick) punishment;
      return makeItem(Material.IRON_BOOTS, "§bKick", "§7Powód: §f" + safe(kick.getReason()),
          "§7Przez: §f" + safe(kick.getKickedBy()), "§7Data: §f" + formatDate(kick.getDate()));
    }

    return new ItemStack(Material.AIR);
  }

  private ItemStack makeItem(Material mat, String name, String... lore) {
    return new ItemBuilder(mat).setName(name).setLore(Arrays.asList(lore)).toItemStack();
  }

  private String formatDate(long ts) {
    if (ts <= 0) {
      return "§cBrak";
    }

    Instant instant = Instant.ofEpochMilli(ts);
    return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        .withZone(java.time.ZoneId.systemDefault())

        .format(java.time.Instant.ofEpochMilli(ts));

  }


  private String safe(String s) {
    return (s == null || s.trim().isEmpty()) ? "—" : s;
  }
}