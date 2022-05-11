package br.com.treinaweb.twbiblioteca.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.treinaweb.twbiblioteca.builders.ClienteBuilder;
import br.com.treinaweb.twbiblioteca.builders.EmprestimoBuilder;
import br.com.treinaweb.twbiblioteca.builders.ObraBuilder;
import br.com.treinaweb.twbiblioteca.dao.EmprestimoDAO;
import br.com.treinaweb.twbiblioteca.enums.Reputacao;
import br.com.treinaweb.twbiblioteca.models.Obra;

@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceTest {
	
	@Mock
	private EmprestimoDAO emprestimoDAO;
	
	@Mock
	private NotificacaoService notificacaoService;
	
	@InjectMocks
	private EmprestimoService emprestimoService;

	@Test
	void quandoMetodoNovoEmprestimoForChamadoDeveRetornarUmEmprestimo() {
		// cenario
		var cliente = ClienteBuilder.builder().build();
		var obra = ObraBuilder.builder().build();

		// execução
		var emprestimo = emprestimoService.novoEmprestimo(cliente, List.of(obra));

		// verificação
		assertEquals(cliente, emprestimo.getCliente());
		assertEquals(List.of(obra), emprestimo.getObras());
		assertEquals(LocalDate.now(), emprestimo.getDataEmprestimo());
		assertEquals(LocalDate.now().plusDays(3), emprestimo.getDataDevolucao());
	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComClienteDeReputacaoRuimDeveRetornarEmprestimoComDevolucaoDeUmDia() {
		// cenario
		var cliente = ClienteBuilder.builder().reputacao(Reputacao.RUIM).build();
		var obra = ObraBuilder.builder().build();

		// execução
		var emprestimo = emprestimoService.novoEmprestimo(cliente, List.of(obra));

		// verificação
		assertEquals(LocalDate.now().plusDays(1), emprestimo.getDataDevolucao());
	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComClienteDeReputacaoRegularDeveRetornarEmprestimoComDevolucaoDeTresDias() {
		// cenario
		var cliente = ClienteBuilder.builder().build();
		var obra = ObraBuilder.builder().build();

		// execução
		var emprestimo = emprestimoService.novoEmprestimo(cliente, List.of(obra));

		// verificação
		assertEquals(LocalDate.now().plusDays(3), emprestimo.getDataDevolucao());
	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComClienteDeReputacaoBoaDeveRetornarEmprestimoComDevolucaoDeCincoDias() {
		// cenario
		var cliente = ClienteBuilder.builder().reputacao(Reputacao.BOA).build();
		var obra = ObraBuilder.builder().build();

		// execução
		var emprestimo = emprestimoService.novoEmprestimo(cliente, List.of(obra));

		// verificação
		assertEquals(LocalDate.now().plusDays(5), emprestimo.getDataDevolucao());
	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComObraNulaDeveLancarUmaExcecaoDoTipoIllegalArgumentException() {
		// cenario
		var cliente = ClienteBuilder.builder().build();
		var mensagemErro = "Obra/obras não pode(m) ser nula(s) ou vazia(s)";

		// execução
		var exception = assertThrows(IllegalArgumentException.class,
				() -> emprestimoService.novoEmprestimo(cliente, null));

		// verificação
		assertEquals(mensagemErro, exception.getMessage());
	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComObraVaziaDeveLancarUmaExcecaoDoTipoIllegalArgumentException() {

		// cenario
		var cliente = ClienteBuilder.builder().build();
		var obras = new ArrayList<Obra>();
		var mensagemErro = "Obra/obras não pode(m) ser nula(s) ou vazia(s)";

		// execução
		var exception = assertThrows(IllegalArgumentException.class,
				() -> emprestimoService.novoEmprestimo(cliente, obras));

		// verificação
		assertEquals(mensagemErro, exception.getMessage());

	}

	@Test
	void quandoMetodoNovoEmprestimoForChamadoComClienteNuloDeveLancarUmaExcecaoDoTipoIllegalArgumentException() {
		// cenario
		var obra = ObraBuilder.builder().build();
		var mensagemErro = "Cliente não pode ser nulo ou vazio";
		
		// execução
		var exception = assertThrows(IllegalArgumentException.class, () -> emprestimoService.novoEmprestimo(null, List.of(obra)));

		// verificação
		assertEquals(mensagemErro, exception.getMessage());

	}
	
	@Test
	void quandoMetodoNotificarAtrasosForChamadoDeveRetornarONumeroDeNotificacoes() {
		//cenario --> deve popular uma lista de emprestimos
		var emprestimos = List.of(
				EmprestimoBuilder.builder().build(), //1. data sem atraso
				EmprestimoBuilder.builder().dataDevolucao(LocalDate.now().minusDays(1)).build() //2. um dia de atraso
				);
		
		when(emprestimoDAO.buscarTodos()).thenReturn(emprestimos);
		
		//execucao
		emprestimoService.notificarAtrasos();
		
		//verficacao
		verify(notificacaoService).notificar(emprestimos.get(1));
//		verify(notificacaoService, Mockito.times(2)).notificar(Mockito.any());
	}
	
}
