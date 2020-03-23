package br.com.sankhya.mm.boleto;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.comercial.BoletoHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoletoButton implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbcWrapper = entityFacade.getJdbcWrapper();

        try {
            jdbcWrapper.openSession();

            final BoletoDao boletoDao = new BoletoDao(jdbcWrapper);
            final List<BigDecimal> lista = new ArrayList<>();

            for(final Registro registro : contextoAcao.getLinhas()) {
                final BigDecimal nufin = (BigDecimal)registro.getCampo("NUFIN");
                lista.add(nufin);
            }

            final BoletoHelper boletoHelper = new BoletoHelper();
            final BoletoHelper.ConfiguracaoBoleto cfg = new BoletoHelper.ConfiguracaoBoleto();

            cfg.setDupRenegociadas(false);
            cfg.setAlterarTipo(false);
            cfg.setTipoTitulo(new BigDecimal(-1));
            cfg.setRegistraConta(false);
            cfg.setUsaContaBcoFinanceiros(true);
            cfg.setBcoIgualConta(true);
            cfg.setEmpIgualConta(true);
            cfg.setGerarNumeroBoleto(true);
            cfg.setReimprimirBoleta(true);
            cfg.setApenasPreparar(true);
            cfg.setEnviarEmail(false);
            cfg.setEnviarEmailParceiro(false);
            
            final List<BigDecimal> ordenados = boletoDao.getFinanceirosPendentesOrdenados(lista);
            for(final BigDecimal nufin : ordenados) {
                cfg.setFinanceirosSelecionados(Arrays.asList(nufin));
                boletoHelper.gerarBoleto(cfg);
            }

            final List<BigDecimal> financeiros = boletoDao.getFinanceiros(lista);
            
            cfg.setReimprimirBoleta(true);
            cfg.setGerarNumeroBoleto(true);
            cfg.setTipoSaidaBoleto(BoletoHelper.SAIDA_BOLETO_IMPRESSAO);
            cfg.setAgrupamentoBoleto(4);
            cfg.setTipoOrdemParceiro(BoletoHelper.ORDEM_PARCEIRO_NENHUMA);
            cfg.setApenasPreparar(false);
            cfg.setFinanceirosSelecionados(financeiros);
            boletoHelper.gerarBoleto(cfg);

        } catch (final Exception exception) {
            contextoAcao.mostraErro(ExceptionUtil.toString(exception));

        } finally {
            JdbcWrapper.closeSession(jdbcWrapper);
        }
    }
}
