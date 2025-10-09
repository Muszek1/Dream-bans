// java
package cc.dreamcode.bans;

import cc.dreamcode.bans.command.*;
import cc.dreamcode.bans.command.handler.InvalidInputHandlerImpl;
import cc.dreamcode.bans.command.handler.InvalidPermissionHandlerImpl;
import cc.dreamcode.bans.command.handler.InvalidSenderHandlerImpl;
import cc.dreamcode.bans.command.handler.InvalidUsageHandlerImpl;
import cc.dreamcode.bans.command.result.BukkitNoticeResolver;
import cc.dreamcode.bans.config.PluginConfig;
import cc.dreamcode.bans.listener.*;
import cc.dreamcode.bans.profile.ProfileCache;
import cc.dreamcode.bans.profile.ProfileController;
import cc.dreamcode.bans.profile.ProfileRepository;
import cc.dreamcode.bans.profile.ProfileService;
import cc.dreamcode.bans.service.BanService;
import cc.dreamcode.bans.service.DiscordWebhookService;
import cc.dreamcode.bans.service.MuteService;
import cc.dreamcode.bans.utils.MessageBroadcaster;
import cc.dreamcode.command.bukkit.BukkitCommandProvider;
import cc.dreamcode.menu.bukkit.BukkitMenuProvider;
import cc.dreamcode.menu.serializer.MenuBuilderSerializer;
import cc.dreamcode.notice.serializer.BukkitNoticeSerializer;
import cc.dreamcode.platform.DreamVersion;
import cc.dreamcode.platform.bukkit.DreamBukkitConfig;
import cc.dreamcode.platform.bukkit.DreamBukkitPlatform;
import cc.dreamcode.platform.bukkit.component.ConfigurationResolver;
import cc.dreamcode.platform.component.ComponentService;
import cc.dreamcode.platform.other.component.DreamCommandExtension;
import cc.dreamcode.platform.persistence.DreamPersistence;
import cc.dreamcode.platform.persistence.component.DocumentPersistenceResolver;
import cc.dreamcode.platform.persistence.component.DocumentRepositoryResolver;
import cc.dreamcode.utilities.adventure.AdventureProcessor;
import cc.dreamcode.utilities.adventure.AdventureUtil;
import cc.dreamcode.utilities.bukkit.StringColorUtil;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.Getter;
import lombok.NonNull;


public final class Bans extends DreamBukkitPlatform implements DreamBukkitConfig, DreamPersistence  {

    @Getter private static Bans instance;

    @Override
    public void load(@NonNull ComponentService componentService) {
        instance = this;

        AdventureUtil.setRgbSupport(true);
        StringColorUtil.setColorProcessor(new AdventureProcessor());
    }

    @Inject
    private ProfileService profileService;
    @Override
    public void enable(@NonNull ComponentService componentService) {



        componentService.setDebug(false);

        this.registerInjectable(BukkitTasker.newPool(this));
        this.registerInjectable(BukkitMenuProvider.create(this));
        this.registerInjectable(BukkitCommandProvider.create(this));
        componentService.registerExtension(DreamCommandExtension.class);
        componentService.registerResolver(ConfigurationResolver.class);

        componentService.registerComponent(cc.dreamcode.bans.config.PluginConfig.class);
        componentService.registerComponent(cc.dreamcode.bans.config.MessageConfig.class);


        componentService.registerComponent(BukkitNoticeResolver.class);
        componentService.registerComponent(InvalidInputHandlerImpl.class);
        componentService.registerComponent(InvalidPermissionHandlerImpl.class);
        componentService.registerComponent(InvalidSenderHandlerImpl.class);
        componentService.registerComponent(InvalidUsageHandlerImpl.class);
        componentService.registerComponent(PluginConfig.class, pluginConfig -> {
            // register persistence + repositories
            this.registerInjectable(pluginConfig.storageConfig);

            componentService.registerResolver(DocumentPersistenceResolver.class);
            componentService.registerComponent(DocumentPersistence.class);
            componentService.registerResolver(DocumentRepositoryResolver.class);

            // enable additional logs and debug messages
            componentService.setDebug(pluginConfig.debug);

            // let the DocumentRepositoryResolver create the repository implementation
            componentService.registerComponent(ProfileRepository.class);

            String webhookUrl = pluginConfig.discordWebhookUrl; // If this method exists
            componentService.registerComponent(DiscordWebhookService.class, service -> {
                service.setWebhookUrl(webhookUrl);
            });

        });

        componentService.registerComponent(ProfileCache.class);
        componentService.registerComponent(ProfileService.class);
        componentService.registerComponent(ProfileController.class);

        componentService.registerComponent(BanService.class);
        componentService.registerComponent(MuteService.class);

        componentService.registerComponent(BanCommand.class);
        componentService.registerComponent(UnbanCommand.class);
        componentService.registerComponent(TempbanCommand.class);
        componentService.registerComponent(KickCommand.class);
        componentService.registerComponent(WarnCommand.class);
        componentService.registerComponent(SilentKickCommand.class);
        componentService.registerComponent(SilentbanCommand.class);
        componentService.registerComponent(SilenttempbanCommand.class);
        componentService.registerComponent(BanipCommand.class);
        componentService.registerComponent(UnbanipCommand.class);
        componentService.registerComponent(TempmuteCommand.class);
        componentService.registerComponent(MuteCommand.class);
        componentService.registerComponent(UnmuteCommand.class);
        componentService.registerComponent(CheckBanCommand.class);

        componentService.registerComponent(BanListener.class);
        componentService.registerComponent(MuteListener.class);
        componentService.registerComponent(MuteCommandListener.class);
        componentService.registerComponent(MultiAccountListener.class);
        componentService.registerComponent(CheckBanMenuListener.class);

        componentService.registerComponent(MessageBroadcaster.class);


    }


    @Override
    public void disable() {
        // features need to be call when server is stopping
        if (this.profileService != null) {
            this.profileService.saveAllAsync();
        }
    }


    @Override
    public @NonNull DreamVersion getDreamVersion() {
        return DreamVersion.create("Dream-Bans", "1.0-InDEV", "Muszek_");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> {
            registry.register(new BukkitNoticeSerializer());
            registry.register(new MenuBuilderSerializer());
        };
    }

}