package net.etfbl.service;

import java.util.ArrayList;

import net.etfbl.config.ConfigLoader;
import net.etfbl.model.Book;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class MailService {
	public boolean sendMail(String mailTo, ArrayList<Book> books) {
		final String username = ConfigLoader.getProperty("USERNAME");
		final String password = ConfigLoader.getProperty("PASSWORD");
		ArrayList<File> files = new ArrayList<>();

		for (Book b : books) {
			files.add(getFileWithBook(b, b.getId()));
		}

		File zip = zipFiles(files);

		Properties props = new Properties();
		props.put("mail.smtp.auth", ConfigLoader.getProperty("mail.smtp.auth"));
		props.put("mail.smtp.starttls.enable", ConfigLoader.getProperty("mail.smtp.starttls.enable"));
		props.put("mail.smtp.host", ConfigLoader.getProperty("mail.smtp.host"));
		props.put("mail.smtp.port", ConfigLoader.getProperty("mail.smtp.port"));
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, ConfigLoader.getProperty("mail.from")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
			message.setSubject(ConfigLoader.getProperty("mail.subject"));

			MimeBodyPart textPart = new MimeBodyPart();
			String text = "";
			for (Book b : books) {
				text += b.toString();
			}
			textPart.setText(text);

			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.attachFile(zip);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(textPart);
			multipart.addBodyPart(attachmentPart);

			message.setContent(multipart);

			Transport.send(message);

			System.out.println("Done");

		} catch (Exception e) {
			System.out.println("Error while send mail.");
			e.printStackTrace();
		}

		return true;
	}

	private File zipFiles(ArrayList<File> files) {
		File file = new File(ConfigLoader.getProperty("path"),String.valueOf(System.currentTimeMillis()) + ".zip");
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
			for (File f : files) {
				try (FileInputStream fis = new FileInputStream(f)) {
					ZipEntry zipEntry = new ZipEntry(f.getName());
					zipOutputStream.putNextEntry(zipEntry);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = fis.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, len);
					}

					zipOutputStream.closeEntry();
				}
			}
		} catch (Exception e) {
			System.out.println("Error while zipping files.");
		}
		return file;
	}

	private File getFileWithBook(Book book, String id) {
		File file = new File(ConfigLoader.getProperty("path"),String.valueOf(System.currentTimeMillis()) + "X" + id + ".txt");
		System.out.println("Putanja: " + file.getAbsolutePath());
		try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			printWriter.println(book.toString());
		} catch (Exception e) {
			System.out.println("Error ocupied in get file with book.");
		}

		return file;
	}
}