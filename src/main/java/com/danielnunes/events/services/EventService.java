package com.danielnunes.events.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.danielnunes.events.domain.events.Event;
import com.danielnunes.events.domain.events.EventRequestDTO;
import com.danielnunes.events.repositories.EventRepository;

@Service
public class EventService {
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Value("${aws.bucketName}")
	private String bucketName;
	
	
	
	public Event createEvent(EventRequestDTO data) {
		
		String imgUrl = null;
		
		if(data.image() != null) {
			imgUrl = this.uploadImg(data.image());
		}
		
		Event newEvent = new Event();
		newEvent.setTitle(data.title());
		newEvent.setDescription(data.description());
		newEvent.setEventUrl(data.eventUrl());
		newEvent.setDate(new Date(data.date()));
		newEvent.setImgUrl(imgUrl);
		newEvent.setRemote(data.remote());
		
		eventRepository.save(newEvent);
		
		return newEvent;
	}
	
	private String uploadImg(MultipartFile multipartFile) {
		String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
		try {
			File file = this.convertMultipartToFile(multipartFile);
			s3Client.putObject(bucketName, fileName, file);
			file.delete();
			return s3Client.getUrl(bucketName, fileName).toString();
		} catch(Exception e) {
			System.out.println("Erro ao subir arquivo!");
			return null;
		}
		
	}
	
	private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
		
		File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(multipartFile.getBytes());
		fos.close();
		return convFile;
	}	
	
	
}
