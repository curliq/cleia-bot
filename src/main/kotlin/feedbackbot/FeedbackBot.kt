package feedbackbot

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.awt.Color

class FeedbackBot {

    fun main() {

        val jda = JDABuilder(AccountType.BOT).setToken(Credentials.DISCORD_TOKEN)
        println("start feedbackbot")
        jda.addEventListener(object : ListenerAdapter() {
            override fun onMessageReceived(event: MessageReceivedEvent?) {
                super.onMessageReceived(event)
                val msg = event!!.message.contentRaw
                println(msg)
                if (msg.startsWith(".template")) {
                    event.channel.sendMessage(getTemplateMessage()).queue()
                }
                else if (isFeedbackMessage(event.message.contentRaw) && !event.author.isBot) {
                    val reporter = event.author
                    val type = msg.substring(msg.indexOf("- Type:") + 7, msg.indexOf("- Game:")).trim()
                    val game = msg.substring(msg.indexOf("- Game:") + 7, msg.indexOf("- Title:")).trim()
                    val title = msg.substring(msg.indexOf("- Title:") + 8, msg.indexOf("- Description:")).trim()
                    val description = msg.substring(msg.indexOf("- Description:") + 14, msg.length).trim()

                    if (!type.equals("UI", true) &&
                            !type.equals("Mechanics", true) &&
                            !type.equals("Balance") &&
                            !type.equals("Other", true)) {
                        event.channel.sendMessage(
                                "Invalid type. Options are \"UI\", \"Mechanics\", \"Balance\" and \"Other\"").queue()
                        return
                    }
                    if (!game.equals("arena", true) && !type.equals("royale", true)) {
                        event.channel.sendMessage("Invalid game. Options are \"Arena\", \"Royale\" and \"Both\"").queue()
                        return
                    }

                    val msgEmbedBuilder = EmbedBuilder()
                    msgEmbedBuilder.setColor(Color(0xefc327))
                    msgEmbedBuilder.setTitle("Feedback submission")
                    msgEmbedBuilder.addBlankField(false)
                    msgEmbedBuilder.addField("Type", type, true)
                    msgEmbedBuilder.addField("Game", game, true)
                    msgEmbedBuilder.addBlankField(false)
                    msgEmbedBuilder.addField("Title", title, false)
                    msgEmbedBuilder.addBlankField(false)
                    msgEmbedBuilder.addField("Description", description, false)
                    msgEmbedBuilder.addBlankField(false)
                    msgEmbedBuilder.setFooter("Submitted by ${reporter.name}#${reporter.discriminator}", reporter.avatarUrl)
//                    event.guild.getTextChannelById(Constants.FEEDBACK_CHANNEL_ID).sendMessage("").queue()
                    event.channel.sendMessage(msgEmbedBuilder.build()).queue()
                }
            }
        })

        jda.build()
    }

    fun getTemplateMessage(): String {
        return "To submit feedback please use the following format to ensure your input gets sent " +
                "to the feedback channel:\n\n" +
                "**- Type:** <Type of feedback> (Options: UI, Mechanics, Balance and Other)\n" +
                "**- Game:** <The game \"mode\" it is regarding> (Options: Arena, Royale and Both)\n" +
                "**- Title:** <Describe your input in one sentence>\n" +
                "**- Description:** <Describe your input fully>\n\n" +
                "Copy paste the template to start."
    }

    fun isFeedbackMessage(message: String): Boolean {
        return message.contains("Type:") &&
                message.contains("Game:") &&
                message.contains("Title:") &&
                message.contains("Description")
    }
}
