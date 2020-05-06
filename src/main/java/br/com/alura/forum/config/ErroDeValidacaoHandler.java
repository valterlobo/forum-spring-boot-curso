package br.com.alura.forum.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroDeValidacaoHandler {

	@Autowired
	private MessageSource messageSource;

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErrorFormularioDTO> handle(MethodArgumentNotValidException exception) {

		List<FieldError> fieldErros = exception.getBindingResult().getFieldErrors();
		List<ErrorFormularioDTO> listErrosDTO = new ArrayList<ErrorFormularioDTO>();
		fieldErros.forEach(errorField -> {
			String mensagem = messageSource.getMessage(errorField, LocaleContextHolder.getLocale());
			ErrorFormularioDTO erroDTO = new ErrorFormularioDTO(errorField.getField(), mensagem);
			listErrosDTO.add(erroDTO);
		});
		return listErrosDTO;
	}

}
