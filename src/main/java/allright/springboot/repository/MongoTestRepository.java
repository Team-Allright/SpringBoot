package allright.springboot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import allright.springboot.model.MongoTest;

public interface MongoTestRepository extends MongoRepository<MongoTest, String> {

}
