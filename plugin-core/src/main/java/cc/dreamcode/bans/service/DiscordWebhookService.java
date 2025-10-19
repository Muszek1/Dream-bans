package cc.dreamcode.bans.service;

import cc.dreamcode.bans.config.PluginConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.okaeri.injector.annotation.Inject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookService {

  private final PluginConfig pluginConfig;
  private final String webhookUrl;

  @Inject
  public DiscordWebhookService(PluginConfig pluginConfig) {
    this.pluginConfig = pluginConfig;
    this.webhookUrl = pluginConfig.discordWebhookUrl;
  }

  public void sendBanEmbed(String target, String admin, String reason, String type, long until) {
    if (this.webhookUrl == null || this.webhookUrl.isEmpty()) return;

    try {
      URL url = new URL(this.webhookUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");

      JsonObject embed = new JsonObject();
      embed.addProperty("title", "🚫 Dream-Bans | " + type);
      embed.addProperty("color", 16711680);

      JsonArray fields = new JsonArray();

      JsonObject f1 = new JsonObject();
      f1.addProperty("name", "Gracz");
      f1.addProperty("value", target);
      f1.addProperty("inline", true);
      fields.add(f1);

      JsonObject f2 = new JsonObject();
      f2.addProperty("name", "Administrator");
      f2.addProperty("value", admin);
      f2.addProperty("inline", true);
      fields.add(f2);

      JsonObject f3 = new JsonObject();
      f3.addProperty("name", "Powód");
      f3.addProperty("value", reason);
      fields.add(f3);

      if (until > 0) {
        JsonObject f4 = new JsonObject();
        f4.addProperty("name", "Wygasa");
        f4.addProperty("value",
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(until)));
        fields.add(f4);
      }

      embed.add("fields", fields);

      JsonObject footer = new JsonObject();
      footer.addProperty("text", "dreamcode.cc / bans");
      embed.add("footer", footer);

      JsonArray embeds = new JsonArray();
      embeds.add(embed);

      JsonObject payload = new JsonObject();
      payload.add("embeds", embeds);

      byte[] out = payload.toString().getBytes(StandardCharsets.UTF_8);
      try (OutputStream os = connection.getOutputStream()) {
        os.write(out);
      }

      connection.getInputStream().close();
      connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
