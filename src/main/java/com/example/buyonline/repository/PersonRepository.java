package com.example.buyonline.repository;

import com.example.buyonline.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    @Query(value = "SELECT * FROM person WHERE tel = ?1 AND password = ?2", nativeQuery = true)
    Person chooseTP(String tel, String password);

    @Query(value = "SELECT * FROM person WHERE tel = ?1", nativeQuery = true)
    Person validateTel(String tel);

    @Query(value = "SELECT * FROM person WHERE id = ?1 AND role = 0", nativeQuery = true)
    Person chooseById(Integer id);

}
