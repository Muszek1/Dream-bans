package cc.dreamcode.bans.command;

import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.punishments.Warn;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.OptArg;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "warn")
@Permission("dream-bans.warn")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WarnCommand implements CommandBase {

  private final cc.dreamcode.bans.config.MessageConfig messageConfig;
  private final ProfileService profileService;
  private final BukkitTasker tasker;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Ostrzega gracza.")
  public void warnPlayer(CommandSender sender, @Arg("target") Player target,
      @OptArg("reason") String reason) {

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
                Warn warn = new Warn(finalReason, sender.getName(), System.currentTimeMillis());
                profile.getWarns().add(warn);
                profile.save();
              })
              .runSync(() -> {
                this.messageConfig.actionBarWarn.with("player", target.getName())
                    .with("issuer", sender.getName()).with("reason", finalReason).with("&", "§").send(target);

                this.messageConfig.warnNotify.with("player", target.getName())
                    .with("issuer", sender.getName()).with("reason", finalReason).send(sender);
              })
              .execute();
        });
  }
}