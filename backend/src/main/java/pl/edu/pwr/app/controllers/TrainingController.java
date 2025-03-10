/* This source code is licensed under MIT License (the "License").
   You may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   https://opensource.org/licenses/MIT

 */
package pl.edu.pwr.app.controllers;

import javassist.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pwr.app.constant.FileConstants;
import pl.edu.pwr.app.exception.domain.IncorrectFileTypeException;
import pl.edu.pwr.app.exception.domain.NotAnImageFileException;
import pl.edu.pwr.app.models.Training;
import pl.edu.pwr.app.repositories.TrainingRepository;
import pl.edu.pwr.app.response.HttpResponse;
import pl.edu.pwr.app.service.TokenBlackListService;
import pl.edu.pwr.app.service.impl.TrainingService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.*;

import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.Paths.get;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = { "/", "/trainingImages"})
public class TrainingController {

    private final TrainingRepository trainingRepository;
    private final TokenBlackListService tokenBlackListService;
    private final TrainingService trainingService;


    public TrainingController(TrainingRepository trainingRepository, TokenBlackListService tokenBlackListService, TrainingService trainingService) {
        this.trainingRepository = trainingRepository;
        this.tokenBlackListService = tokenBlackListService;
        this.trainingService = trainingService;
    }



    @GetMapping("/trainings")
    public Page<Training> list(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size, Authentication authentication , HttpServletRequest request) throws ServletException {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Training> pageResult = trainingRepository.findAll(pageRequest);
            List<Training> trainings = pageResult
                    .stream()
                    .map(Training::new)
                    .collect(toList());
            Collections.sort(trainings);
            return new PageImpl<>(trainings, pageRequest, pageResult.getTotalElements());

        }
    @GetMapping("/three_trainings")
    public List<Training> geThreeTasks() {
        //Wyswietlenie trzech najblizszych szkolen dla niezalogowanego uzytkownika
        return getTrainingsAsUser(3);

    }

    @DeleteMapping("trainings/delete/{id}")
    //@PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) throws IOException {
        trainingService.deleteTraining(id);
        return response(HttpStatus.OK, "Deleted successfully");
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    @GetMapping("/find_training/{id}")
    public Training getUser(@PathVariable("id") long id) {
        return trainingRepository.findById(id);
    }

    public List<Training> getSortedTrainings(){
        List<Training> trainingList = (List<Training>) trainingRepository.findAll();
        Collections.sort(trainingList);
        return trainingList;
    }


    public List<Training> getTrainingsAsUser(int limit){
        List<Training> sortedTrainingList = getSortedTrainings();
        LocalDate localDate = null;
        LocalTime localTime = null;
        List<Training> trainingListUser = new ArrayList<>();
        List<Training> trainingPassed = new ArrayList<>();
        int i=0;
        for (Training training : sortedTrainingList) {
            if(training.getDate().isAfter(localDate.now())){
                trainingListUser.add(training);
                i++;
            }
            else if (training.getDate() == localDate.now()) {
                    if (training.getTime().isAfter(localTime.now())) {
                        trainingListUser.add(training);
                        i++;
                    }
                }
            else {
                trainingPassed.add(0,training);
            }

            if(i>limit){
                break;
            }
        }
        if(limit>trainingListUser.size()){
            int difference = limit - trainingListUser.size();
            for( int j = 0 ; j<difference;j++){
                if(trainingPassed.size()>j){
                trainingListUser.add(0,trainingPassed.get(j));
                }
            }
        }

        return trainingListUser;
    }
    public List<Training> getTrainingAsAdmin(){
        return (List<Training>) trainingRepository.findAll();
    }

    @GetMapping(path = "/image/{id}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTrainingImage(@PathVariable("id") String trainingId, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstants.USER_FOLDER + trainingId + FileConstants.FORWARD_SLASH + fileName));
    }

//    @GetMapping(path = "/file/{id}/{fileName}", produces = ALL_VALUE)
//    public byte[] getTrainingFile(@PathVariable("id") String trainingId, @PathVariable("fileName") String fileName) throws IOException {
//        return Files.readAllBytes(Paths.get(FileConstants.USER_FOLDER + trainingId + FileConstants.FORWARD_SLASH + fileName));
//    }

//    @PostMapping("/update")
//    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
//                                       @RequestParam("firstName") String firstName,
//                                       @RequestParam("lastName") String lastName,
//                                       @RequestParam("username") String username,
//                                       @RequestParam("email") String email,
//                                       @RequestParam("role") String role,
//                                       @RequestParam("isActive") String isActive,
//                                       @RequestParam("isNonLocked") String isNonLocked,
//                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
//        return new ResponseEntity<>(updatedUser, OK);
//    }
    @PostMapping("/training/update")
    ResponseEntity<Training> update(@RequestParam("id") long id,
                                        @RequestParam("topic") String topic,
                                         @RequestParam("description") String description,
                                         @RequestParam("trainer") String trainer,
                                         @RequestParam("durationInMinutes") int durationInMinutes,
                                         @RequestParam(value = "trainingImage", required = false) MultipartFile trainingImage,
                                         @RequestParam(value = "trainingFile", required = false) MultipartFile trainingFile ) throws IOException, NotAnImageFileException, IncorrectFileTypeException, NotFoundException {
        Training training = trainingService.updateTraining(id, topic, description, trainer, durationInMinutes, trainingImage, trainingFile);

        return new ResponseEntity<>(training, HttpStatus.OK);

    }



    @GetMapping("/file/{id}/{fileName}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable("id") String trainingId, @PathVariable("fileName") String filename) throws IOException {
        Path filePath = get(FileConstants.USER_FOLDER + trainingId + FileConstants.FORWARD_SLASH).toAbsolutePath().normalize().resolve(filename);
        if(!Files.exists(filePath)) {
            throw new FileNotFoundException(filename + " was not found on the server");
        }
        Resource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders).body(resource);
    }



    @PostMapping("/trainings")
    ResponseEntity<Training> addTraining(@RequestParam("topic") String topic,
                                         @RequestParam("description") String description,
                                         @RequestParam("trainer") String trainer,
                                         @RequestParam("durationInMinutes") int durationInMinutes,
                                         @RequestParam(value = "trainingImage", required = false) MultipartFile trainingImage,
                                         @RequestParam(value = "trainingFile", required = false) MultipartFile trainingFile ) throws IOException, NotAnImageFileException, IncorrectFileTypeException {
        Training training = trainingService.addNewTraining(topic, description, trainer, durationInMinutes, trainingImage, trainingFile);

        return new ResponseEntity<>(training, HttpStatus.OK);

    }

    
}