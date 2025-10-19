package cc.dreamcode.bans.service;

import cc.dreamcode.bans.Bans;
import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.punishments.Ban;
import cc.dreamcode.bans.punishments.IpBan;
import cc.dreamcode.bans.punishments.TempBan;
import cc.dreamcode.bans.utils.MessageBroadcaster;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BanService {

  private final Bans plugin;
  private final BukkitTasker tasker;
  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final PluginConfig pluginConfig;
  private final DiscordWebhookService discordWebhookService;

  public void createBan(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName, String reason) {

    final Player online = Bukkit.getPlayer(targetUuid);
    if (online != null && online.hasPermission(this.pluginConfig.BanProtectedPermission)) {
      this.messageConfig.banProtected.with("player", targetName).send(sender);
      return;
    }

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }
    final String finalReason = reason;

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.ban(finalReason, sender.getName());

      Ban ban = new Ban(finalReason, System.currentTimeMillis(), sender.getName(), 0L);
      profile.getBans().add(ban);

      this.profileService.saveAsync(profile);

      this.kickAndBroadcastBan(sender, targetName, finalReason, online, this.messageConfig.banKick,
          this.messageConfig.banBroadcast, "Ban", 0L);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), finalReason, "Ban", 0L);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się utworzyć bana dla " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void createTempBan(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName, long duration, String reason) {

    final Player online = Bukkit.getPlayer(targetUuid);
    if (online != null && online.hasPermission(this.pluginConfig.BanProtectedPermission)) {
      this.messageConfig.banProtected.with("player", targetName).send(sender);
      return;
    }

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }
    final String finalReason = reason;

    long start = System.currentTimeMillis();
    long until = start + duration;

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.ban(finalReason, sender.getName());
      profile.setBanUntil(until);

      TempBan tempBan = new TempBan(finalReason, start, sender.getName(), until);
      profile.getTempBans().add(tempBan);

      this.profileService.saveAsync(profile);

      this.kickAndBroadcastBan(sender, targetName, finalReason, online,
          this.messageConfig.tempBanKick, this.messageConfig.tempBanBroadcast, "TempBan", until);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), finalReason, "TempBan",
          until);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się utworzyć tymczasowego bana dla " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void createIpBan(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName, @NonNull String ip, String reason) {

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }
    final String finalReason = reason;

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.banIp(ip, finalReason, sender.getName());

      IpBan ipBan = new IpBan(ip, finalReason, System.currentTimeMillis(), sender.getName(), 0L);
      profile.getIpBansCheck().add(ipBan);

      this.profileService.saveAsync(profile);

      Player online = Bukkit.getPlayer(targetUuid);
      if (online != null) {
        String kickMsg = this.messageConfig.ipBanKick.replace("{reason}", finalReason)
            .replace("{bannedBy}", sender.getName()).replace("&", "§");
        online.kickPlayer(kickMsg);
      }

      MessageBroadcaster.broadcast(this.messageConfig.ipBanBroadcast, targetName, sender.getName(),
          "IP-Ban", finalReason, 0);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), finalReason, "IP-Ban",
          0L);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się utworzyć IP-bana dla " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void removeBan(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName) {

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.unban();
      this.profileService.saveAsync(profile);

      Player online = Bukkit.getPlayer(targetUuid);
      if (online != null) {
        this.messageConfig.unbanNotify.send(online);
      }
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się usunąć bana gracza " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void removeIpBan(@NonNull CommandSender sender, @NonNull String ip) {

    this.tasker.newChain()
        .run(() -> this.profileService.getProfileCache().all().values().forEach(profile -> {
          if (profile.hasIpBan(ip)) {
            profile.unbanIp(ip);
            this.profileService.saveAsync(profile);
          }
        })).execute();
  }

  public void removeIpBan(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName) {

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      if (profile.getLastIp() != null) {
        profile.unbanIp(profile.getLastIp());
        this.profileService.saveAsync(profile);
      }
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się usunąć IP-bana gracza " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  private void kickAndBroadcastBan(CommandSender sender, String targetName, String reason,
      Player online, String kickTemplate, String broadcastTemplate, String type, long until) {

    Bukkit.getScheduler().runTask(this.plugin, () -> {
      if (online != null) {
        String msg = kickTemplate.replace("{reason}", reason).replace("{target}", targetName)
            .replace("{bannedBy}", sender.getName()).replace("{banExpire}",
                until > 0 ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(until))
                    : "PERM").replace("&", "§");
        online.kickPlayer(msg);
      }
    });

    MessageBroadcaster.broadcast(broadcastTemplate, targetName, sender.getName(), type, reason,
        until);
  }
}
