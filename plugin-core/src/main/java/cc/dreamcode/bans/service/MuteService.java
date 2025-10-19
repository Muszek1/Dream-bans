package cc.dreamcode.bans.service;

import cc.dreamcode.bans.Bans;
import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.punishments.Mute;
import cc.dreamcode.bans.punishments.TempMute;
import cc.dreamcode.bans.utils.MessageBroadcaster;
import eu.okaeri.injector.annotation.Inject;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MuteService {

  private final Bans plugin;
  private final ProfileService profileService;
  private final MessageConfig messageConfig;
  private final DiscordWebhookService discordWebhookService;

  public void createMute(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName, String reason) {

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }
    final String finalReason = reason;

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.mute(finalReason, sender.getName());

      Mute mute = new Mute(finalReason, sender.getName(), System.currentTimeMillis());
      profile.getMutes().add(mute);

      this.profileService.saveAsync(profile);

      MessageBroadcaster.broadcast(this.messageConfig.muteBroadcast, targetName, sender.getName(),
          "Mute", finalReason, 0);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), finalReason, "Mute", 0);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się utworzyć muta dla " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void createTempMute(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName, long duration, String reason) {

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }
    final String finalReason = reason;

    long start = System.currentTimeMillis();
    long until = start + duration;

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.mute(finalReason, sender.getName());
      profile.setMuteUntil(until);

      TempMute tempMute = new TempMute(finalReason, start, sender.getName(), until);
      profile.getTempMutes().add(tempMute);

      this.profileService.saveAsync(profile);

      MessageBroadcaster.broadcast(this.messageConfig.tempMuteBroadcast, targetName,
          sender.getName(), "TempMute", finalReason, until);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), finalReason, "TempMute",
          until);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się utworzyć tymczasowego muta dla " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }

  public void removeMute(@NonNull CommandSender sender, @NonNull UUID targetUuid,
      @NonNull String targetName) {

    this.profileService.loadAsync(targetUuid, targetName).thenAccept(profile -> {
      profile.unmute();
      this.profileService.saveAsync(profile);

      this.messageConfig.unmuteNotify.send(sender);

      this.discordWebhookService.sendBanEmbed(targetName, sender.getName(), "—", "Unmute", 0);
    }).exceptionally(e -> {
      sender.sendMessage("§cNie udało się usunąć muta gracza " + targetName + ".");
      e.printStackTrace();
      return null;
    });
  }
}
