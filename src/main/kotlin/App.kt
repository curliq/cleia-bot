import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

fun main(args: Array<String>) {

    val jda = JDABuilder(AccountType.BOT).setToken(Secrets.DISCORD_TOKEN)

    jda.addEventListener(object : ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent?) {
            super.onMessageReceived(event)
            if (event!!.message.contentRaw.startsWith(".ign ")) {
                val roleName = "IGN: ${event.message.contentRaw.substring(5, event.message.contentRaw.length)}"
                val userRoles = event.member.roles

                // delete previous IGN roles from user
                for (userCurrentRole in userRoles) {
                    if (userCurrentRole.name.startsWith("IGN:")) {
                        userCurrentRole.delete().queue()
                    }
                }

                event.guild.controller.createRole().setName(roleName).setMentionable(false).queue {
                    event.guild.controller.addSingleRoleToMember(event.member, it).queue()
                }

                event.channel.sendMessage(
                        "Role **$roleName** foi criado e adicionado a **${event.member.effectiveName}**.").queue()

            }
        }
    })

    jda.build()
}
