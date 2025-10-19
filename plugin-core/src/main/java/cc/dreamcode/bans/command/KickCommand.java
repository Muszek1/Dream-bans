package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.punishments.Kick;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.OptArg;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.utilities.builder.MapBuilder;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "kick")
@Permission("dream-bans.kick")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KickCommand implements CommandBase {

  private final MessageConfig messageConfig;
  private final ProfileService profileService;
  private final BukkitTasker tasker;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Wyrzuca gracza z broadcastem i zapisuje w historii.")
  public void kickPlayer(CommandSender sender, @Arg("target") Player target,
      @OptArg("reason") String reason) {
    if (target == null) {
      this.messageConfig.playerNotFound.send(sender);
      return;
    }

    String finalReason = (reason == null || reason.isEmpty())
        ? this.messageConfig.defaultReason
        : reason;

    this.profileService.loadAsync(target.getUniqueId(), target.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            this.tasker.newChain().runSync(() -> {
              this.messageConfig.errorWhenLoadingProfile.with("player", target.getName()).send(sender);
            }).execute();
            return;
          }

          this.tasker.newChain()
              .run(() -> {
                Kick kick = new Kick(finalReason, sender.getName(), System.currentTimeMillis());
                profile.getKicks().add(kick);
                profile.save();
              })
              .runSync(() -> {

                String kickMsg = this.messageConfig.kickFormat.replace("{reason}", finalReason)
                    .replace("{issuer}", sender.getName()).replace("&", "§");
                target.kickPlayer(kickMsg);

                this.messageConfig.kickBroadcast.with(
                    MapBuilder.of("player", target.getName(), "issuer", sender.getName(), "reason",
                        finalReason)).sendAll();

                this.messageConfig.kickSuccess.with("player", target.getName()).with("issuer", sender.getName())
                    .with("reason", finalReason).send(sender);
              })
              .execute();
        });
  }
}