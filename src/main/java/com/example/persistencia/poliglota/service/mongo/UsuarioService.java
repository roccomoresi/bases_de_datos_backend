package com.example.persistencia.poliglota.service.mongo;

import com.example.persistencia.poliglota.model.mongo.UsuarioMongo;
import com.example.persistencia.poliglota.repository.mongo.UsuarioMongoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioMongoRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<UsuarioMongo> getAll() {
        return repository.findAll();
    }

    public UsuarioMongo save(UsuarioMongo usuario) {
        return repository.save(usuario);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public UsuarioMongo findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
