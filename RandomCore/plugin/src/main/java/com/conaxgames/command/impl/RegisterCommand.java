package com.conaxgames.command.impl;

import com.conaxgames.CorePlugin;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandHandler {

    @Command(name = "register", rank = Rank.NORMAL,
            description = "Register your account with our website.")
    public void register(Player player) {
        Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());

        if (mineman == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            try {
                if (CorePlugin.getInstance().getUserManager().isRegistered(player)) {
                    player.sendMessage(CC.RED + "You're already registered.");
                    return;
                }

                String key = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
                CorePlugin.getInstance().getUserManager().registerUser(player, key);
            } catch (Exception e) {
                player.sendMessage(CC.RED + "An error occurred, try again later.");
                e.printStackTrace();
            }
        });
    }

    private boolean isValidEmailAddress(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
        java.util.regex.Matcher m = p.matcher(email);

        return m.matches();
    }

    /*private void sendEmail(String email, String confirmationId) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // TODO: add config options for properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                // TODO: add config options for user & pass
                                return new PasswordAuthentication("admin@minion.lol", "huilttsxlhgysmuj");
                            }
                        }
                );

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("admin@minion.lol", "Bloom"));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Bloom Registration");
                    message.setText(
                            "Thank you for registering, however, in order to complete your registration you must verify your email.\n"
                                    + "Please click on the following link to finish the registration process:\n"
                                    + "https://www.minion.lol/confirm/" + confirmationId + "\n\n"
                                    + "Thanks,\n"
                                    + "Bloom");

                    Transport.send(message);
                }
                catch (MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }*/
}