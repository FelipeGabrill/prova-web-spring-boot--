package web.trab2.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import web.trab2.config.Utils;
import web.trab2.model.Aluno;
import web.trab2.model.AlunoDto;
import web.trab2.repository.AlunoRepository;

@RestController
public class Controller {

    @Autowired
    private AlunoRepository repository;

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<Aluno>> getAll() {
        /*
            Aqui você consulta o Repository para retornar um array list com todos os dados.
            Evidentemente, você deve remover este return null.
         */
    	ArrayList<Aluno> result = repository.findAll();
    	//result.stream().map(x -> new AlunoDto(x)).collect(Collectors.toList());
    	//return ResponseEntity.ok(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/updateAluno")
    public ResponseEntity<Object> updateAluno(@RequestBody AlunoDto dto) {
        /*
            Aqui você atualiza os dados de um aluno. Note que findById te retornará um objeto
            do tipo Optional. Ele poderá indicar se o id do dado realmente existe. Caso exista,
            use o Repository para salvar o objeto Aluno. Se o aluno com o id informado não
            existir, responda status NOT_FOUND como o corpo da mensagem "Não há aluno com este id".
            O último return não pode ser null. Corrija isso.
         */
    	try {
    	    Long alunoId = Long.parseLong(dto.id);
    	    Integer alunoTurma = Integer.parseInt(dto.turma);
    	    Double alunoNota = Double.parseDouble(dto.nota);
    	    
    	    Optional<Aluno> optionalAluno = repository.findById(alunoId);
    	    if (optionalAluno.isPresent()) {
    	        Aluno entity = optionalAluno.get();
    	        entity.setNome(dto.nome);
    	        entity.setMatricula(dto.matricula);
    	        entity.setTurma(alunoTurma);
    	        entity.setNota(alunoNota);

    	        entity = repository.save(entity);
    	        return ResponseEntity.status(HttpStatus.OK).body("Aluno atualizado com sucesso");
    	    } else {
    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não há aluno com esse ID");
    	    }
    	} catch (NumberFormatException e) {
    	    return ResponseEntity.badRequest().body("Valores inválidos no DTO");
    	}
    }

    @PostMapping("/novoAluno")
    public ResponseEntity<Object> novoAluno(@RequestBody AlunoDto dto) {
    	 /*
        	Aqui você registra um novo aluno, que você cria a partir do dto.
        	PRESTE ATENÇÃO. A turma não pode exceder 10 alunos. A interface AlunoRepository
        	contém um recurso para contagem de alunos. Examine e decida como proceder.
        	Se o número de alunos exceder o limite, retorne um erro com status PAYLOAD_TOO_LARGE
        	e agregue no corpo da mensagem a string "Dados em excesso".
        	Evidentemente, remova o null do último return enquanto agrega o status OK.
    	 */
    	Integer alunoTurma;
    	try {
        	alunoTurma = Integer.parseInt(dto.turma);
    	} catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Turma inválida");
        }
        
        if (repository.countAlunoByTurma(alunoTurma) >= 10) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Dados em excesso");
        }
        
        Aluno entity = new Aluno(dto);
        entity = repository.save(entity);
        return ResponseEntity.status(HttpStatus.OK).body("Aluno registrado com sucesso");
    }


    @PostMapping("/delete")
    public ResponseEntity<Object> deleteAluno(@RequestBody String id) {
        /*
            Deixei este código presente por cortesia, assim como os dois métodos abaixo.
         */
        Long alunoId = Long.parseLong(id);
        this.repository.deleteById(alunoId);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/reset")
    public ResponseEntity<Object> reset() {
        this.repository.deleteAll();
        Utils.startDb(this.repository);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/zerar")
    public ResponseEntity<Object> zerar() {
        this.repository.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
