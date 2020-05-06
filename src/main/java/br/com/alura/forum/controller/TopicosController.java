package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.TopicoDTO;
import br.com.alura.forum.controller.dto.TopicoDetalheDTO;
import br.com.alura.forum.controller.form.TopicoEditForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping(value = "/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@GetMapping
	@Cacheable(value="listaDeTopicos")
	public Page<TopicoDTO> lista(@RequestParam(required = false) String nomeCurso, @PageableDefault(sort="id" , direction = Direction.DESC, page=0 , size = 1  )Pageable paginacao) {

		Page<TopicoDTO> topicosDTO = null;

		//Pageable paginacao = PageRequest.of(pagina, qtd, Direction.DESC, ordenacao);

		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			topicosDTO = TopicoDTO.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);

			topicosDTO = TopicoDTO.converter(topicos);
		}
		return topicosDTO;
	}

	@GetMapping("/{id}")
	public ResponseEntity<TopicoDetalheDTO> detalhar(@PathVariable Long id) {

		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {

			return ResponseEntity.ok(new TopicoDetalheDTO(topico.get()));
		}
		return ResponseEntity.notFound().build();

	}

	@PostMapping
	@CacheEvict(value="listaDeTopicos" , allEntries = true)
	public ResponseEntity<TopicoDTO> cadastrar(@RequestBody @Valid TopicoForm topicoForm,
			UriComponentsBuilder uriBuilder) {

		Curso curso = cursoRepository.findByNome(topicoForm.getNomeCurso());
		Topico topico = TopicoForm.converter(topicoForm, curso);
		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDTO(topico));

	}

	@PutMapping("/{id}")
	@CacheEvict(value="listaDeTopicos" , allEntries = true)
	public ResponseEntity<TopicoDetalheDTO> atualizar(@PathVariable Long id,
			@RequestBody @Valid TopicoEditForm topicoEditForm, UriComponentsBuilder uriBuilder) {

		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {
			topico.get().setTitulo(topicoEditForm.getTitulo());
			topico.get().setMensagem(topicoEditForm.getMensagem());
			topicoRepository.save(topico.get());
			return ResponseEntity.ok(new TopicoDetalheDTO(topico.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	@CacheEvict(value="listaDeTopicos" , allEntries = true)
	public ResponseEntity delete(@PathVariable Long id) {

		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();

		}
		return ResponseEntity.notFound().build();

	}

}
