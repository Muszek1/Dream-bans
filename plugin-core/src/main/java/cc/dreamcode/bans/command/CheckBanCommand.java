package cc.dreamcode.bans.command;

import cc.dreamcode.bans.config.MessageConfig;
import cc.dreamcode.bans.gui.CheckBanMenu;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "checkban")
@Permission("dream-bans.checkban")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CheckBanCommand implements CommandBase {

  private final ProfileService profileService;
  private final BukkitTasker tasker;
  private final MessageConfig messageConfig;
  private final CheckBanMenu checkBanMenu;

  @Completion(arg = "target", value = "@allplayers")
  @Executor(description = "Pokazuje historię banów gracza.")
  public void checkBan(CommandSender sender, @Arg("target") OfflinePlayer target) {

    if (!(sender instanceof Player)) {
      sender.sendMessage("§cTylko gracz może użyć tej komendy.");
      return;
    }

    Player player = (Player) sender;

    this.profileService.loadAsync(target.getUniqueId(), target.getName())
        .whenComplete((profile, exception) -> {

          if (exception != null) {
            this.tasker.newChain().runSync(() -> {
              this.messageConfig.errorWhenLoadingProfile.with("player", target.getName()).send(player);
            }).execute();
            return;
          }

          this.tasker.newChain().runSync(() -> {
            this.checkBanMenu.open(profile, player);
          }).execute();
        });
  }
}