package cc.dreamcode.bans.command;

import cc.dreamcode.bans.service.MuteService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.OptArg;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.utilities.ParseUtil;
import eu.okaeri.injector.annotation.Inject;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command(name = "tempmute")
@Permission("dream-bans.tempmute")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TempmuteCommand implements CommandBase {

  private final MuteService muteService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Tymczasowo wycisza gracza.")
  public void tempMutePlayer(CommandSender sender, @Arg("target") OfflinePlayer target,
      @Arg("muteExpire") String muteExpire, @OptArg("reason") String reason) {

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    Optional<Duration> durationOpt = ParseUtil.parsePeriod(muteExpire);
    if (durationOpt.isEmpty()) {
      this.messageConfig.invalidFormat.with("input", muteExpire).send(sender);
      return;
    }
    long millis = durationOpt.get().toMillis();
    this.muteService.createTempMute(sender, target.getUniqueId(), target.getName(), millis, reason);
  }
}
