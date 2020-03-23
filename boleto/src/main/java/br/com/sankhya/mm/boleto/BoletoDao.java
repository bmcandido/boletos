package br.com.sankhya.mm.boleto;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BoletoDao {

    private final JdbcWrapper jdbcWrapper;

    public BoletoDao(final JdbcWrapper jdbcWrapper) {
        this.jdbcWrapper = jdbcWrapper;
    }

    public List<BigDecimal> getFinanceirosPendentesOrdenados(List<BigDecimal> lista) throws Exception {
        final List<BigDecimal> pendentes = new ArrayList<>();

        String registros = null;
        for(final BigDecimal nufin : lista) {
            registros = (registros == null ? "" : (registros + ", ")) + nufin.toString();
        }

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select nufin from v_ordenabol_interno where nufin in (" + registros + ") order by ordenacao");
        try (final ResultSet resultSet = nativeSql.executeQuery()) {
            while (resultSet.next()) {
                final BigDecimal nufin = resultSet.getBigDecimal("nufin");
                System.out.println(nufin.toString());
                pendentes.add(nufin);
            }
        }

        return pendentes;
    }

    public List<BigDecimal> getFinanceiros(List<BigDecimal> lista) throws Exception {
        final List<BigDecimal> financeiros = new ArrayList<>();

        String registros = null;
        for(final BigDecimal nufin : lista) {
            registros = (registros == null ? "" : (registros + ", ")) + nufin.toString();
        }

        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
        nativeSql.appendSql("select nufin from v_ordenabol_interno where nufin in (" + registros + ")");

        try (final ResultSet resultSet = nativeSql.executeQuery()) {
            while (resultSet.next()) {
                final BigDecimal nufin = resultSet.getBigDecimal("nufin");

                financeiros.add(nufin);
            }
        }

        return financeiros;
    }
//
//    public void removerNossoNumero(final BigDecimal nufin) throws Exception {
//        final String sql = "" +
//                "begin " +
//                "  update tgffin set nossonum = null, codigobarra = null, linhadigitavel = null " +
//                "  where nufin = :nufin; " +
//                "end;";
//
//        final Connection connection = jdbcWrapper.getConnection();
//        try (final CallableStatement callableStatement = connection.prepareCall(sql)) {
//
//            callableStatement.setBigDecimal("nufin", nufin);
//
//            callableStatement.execute();
//        }
//    }
}


