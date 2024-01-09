package com.aula.rocketseat.todolist.task;


import com.aula.rocketseat.todolist.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;
    @PostMapping("/")
    public ResponseEntity created(@RequestBody TaskModel taskModel,
                                  HttpServletRequest request) {
        taskModel.setIdUser((UUID) request.getAttribute("idUser") );
        if (LocalDateTime.now().isAfter(taskModel.getStartAt()) || taskModel.getEndAt().isBefore(taskModel.getStartAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a data incio deve ser maior que a data atual /a data final deve ser maior que a data de começo");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        List<TaskModel> tasks = this.taskRepository.findByIdUser((UUID) request.getAttribute("idUser"));
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel,
                       HttpServletRequest request,
                       @PathVariable UUID id){

        var task = this.taskRepository.findById(id).orElse(null);
        if (task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usuario nao tem permisssao");
        }
        Utils.copyNonNullProperties(taskModel,task);

        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(this.taskRepository.save(taskUpdated));
    }

}
