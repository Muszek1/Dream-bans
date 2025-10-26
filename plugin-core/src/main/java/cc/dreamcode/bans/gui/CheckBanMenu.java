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
import cc.dreamcode.utilities.DateUtil;
import cc.dreamcode.utilities.builder.MapBuilder;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import eu.okaeri.injector.annotation.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CheckBanMenu {

  private final PluginConfig pluginConfig;

  public void open(Profile profile, Player viewer) {
    PluginConfig.CheckBanMenuConfig menuConfig = this.pluginConfig.checkBanMenu;
    BukkitMenuBuilder builder = menuConfig.menuBuilder;

    BukkitMenu baseMenu = builder.buildEmpty(
        new MapBuilder<String, Object>().put("name", safe(profile.getName())).build());

    Map<String, Object> summaryPlaceholders = new MapBuilder<String, Object>()
        .put("name", safe(profile.getName()))
        .put("bans_size", profile.getBans().size())
        .put("tempbans_size", profile.getTempBans().size())
        .put("mutes_size", profile.getMutes().size())
        .put("tempmutes_size", profile.getTempMutes().size())
        .put("ipbans_size", profile.getIpBansCheck().size())
        .put("kicks_size", profile.getKicks().size())
        .build();

    ItemStack summaryItem = ItemBuilder.of(menuConfig.summaryItem)
        .fixColors(summaryPlaceholders, true)
        .toItemStack();

    baseMenu.setItem(4, summaryItem);

    BukkitMenuPaginated paginatedMenu = baseMenu.toPaginated();

    paginatedMenu.setPreviousPageSlot(48, menuConfig.previousPageItem, e -> {
    });

    paginatedMenu.setNextPageSlot(50, menuConfig.nextPageItem, e -> {
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
    PluginConfig.PunishmentItemsConfig itemsConfig = this.pluginConfig.punishmentItems;

    if (punishment instanceof Ban) {
      Ban ban = (Ban) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(ban.getReason()))
          .put("bannedBy", safe(ban.getBannedBy()))
          .put("date", formatOrDefault(ban.getDate()))
          .put("until_or_permanent", formatUntilOrPermanent(ban.getUntil()))
          .build();
      return ItemBuilder.of(itemsConfig.ban)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    if (punishment instanceof TempBan) {
      TempBan tempBan = (TempBan) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(tempBan.getReason()))
          .put("bannedBy", safe(tempBan.getBannedBy()))
          .put("date", formatOrDefault(tempBan.getDate()))
          .put("until", formatOrDefault(tempBan.getUntil()))
          .build();
      return ItemBuilder.of(itemsConfig.tempBan)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    if (punishment instanceof Mute) {
      Mute mute = (Mute) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(mute.getReason()))
          .put("mutedBy", safe(mute.getMutedBy()))
          .put("date", formatOrDefault(mute.getDate()))
          .build();
      return ItemBuilder.of(itemsConfig.mute)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    if (punishment instanceof TempMute) {
      TempMute tempMute = (TempMute) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(tempMute.getReason()))
          .put("mutedBy", safe(tempMute.getMutedBy()))
          .put("date", formatOrDefault(tempMute.getDate()))
          .put("until", formatOrDefault(tempMute.getUntil()))
          .build();
      return ItemBuilder.of(itemsConfig.tempMute)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    if (punishment instanceof IpBan) {
      IpBan ipBan = (IpBan) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(ipBan.getReason()))
          .put("bannedBy", safe(ipBan.getBannedBy()))
          .put("date", formatOrDefault(ipBan.getDate()))
          .put("until_or_permanent", formatUntilOrPermanent(ipBan.getUntil()))
          .build();
      return ItemBuilder.of(itemsConfig.ipBan)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    if (punishment instanceof Kick) {
      Kick kick = (Kick) punishment;
      Map<String, Object> placeholders = new MapBuilder<String, Object>()
          .put("reason", safe(kick.getReason()))
          .put("kickedBy", safe(kick.getKickedBy()))
          .put("date", formatOrDefault(kick.getDate()))
          .build();
      return ItemBuilder.of(itemsConfig.kick)
          .fixColors(placeholders, true)
          .toItemStack();
    }

    return new ItemStack(Material.AIR);
  }

  private String formatOrDefault(long timestamp) {
    return (timestamp > 0) ? DateUtil.format(Instant.ofEpochMilli(timestamp)) : "§cBrak";
  }

  private String formatUntilOrPermanent(long timestamp) {
    return (timestamp > 0) ? DateUtil.format(Instant.ofEpochMilli(timestamp)) : "§cPermanentny";
  }

  private String safe(String s) {
    return (s == null || s.trim().isEmpty()) ? "—" : s;
  }
}