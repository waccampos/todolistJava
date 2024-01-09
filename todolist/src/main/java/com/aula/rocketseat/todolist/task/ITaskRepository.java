package com.aula.rocketseat.todolist.task;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

    List<TaskModel> findByIdUser(UUID uuid);

}
