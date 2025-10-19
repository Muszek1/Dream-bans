package cc.dreamcode.bans.command;

import cc.dreamcode.bans.service.BanService;
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

@Command(name = "tempban")
@Permission("dream-bans.tempban")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TempbanCommand implements CommandBase {

  private final BanService banService;
  private final cc.dreamcode.bans.config.MessageConfig messageConfig;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Banuje gracza tymczasowo.")
  public void tempBanPlayer(CommandSender sender, @Arg("target") OfflinePlayer target,
      @Arg("banExpire") String banExpire, @OptArg("reason") String reason) {

    if (reason == null || reason.isEmpty()) {
      reason = this.messageConfig.defaultReason;
    }

    Optional<Duration> durationOpt = ParseUtil.parsePeriod(banExpire);
    if (durationOpt.isEmpty()) {
      this.messageConfig.invalidFormat.with("input", banExpire).send(sender);
      return;
    }
    long millis = durationOpt.get().toMillis();
    this.banService.createTempBan(sender, target.getUniqueId(), target.getName(), millis, reason);
  }
}
