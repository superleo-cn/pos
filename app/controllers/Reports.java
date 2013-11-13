package controllers;

import models.ReportTransaction;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import play.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/13/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Reports extends Basic {

    public static void transaction()  {

        renderJSON(ReportTransaction.search("",null));

    }
    public static void exportTransaction()  {


        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/TransactionDetail.jasper");

        JRDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList("a"));
        JasperPrint print = null;
        try {
            print = JasperFillManager.fillReport(is, null, dataSource);

            ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
            //OutputStream outputfile= new FileOutputStream(new File("c:/output/JasperReport.xls"));

            JRXlsExporter exporterXLS = new JRXlsExporter();
            exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
            exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.out);
            //exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, "dx:/Dashboard2.xls" );
            exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporterXLS.exportReport();
            response.setHeader("Content-Type","application/vnd.ms-excel");
            response.setHeader("Content-Disposition"," attachment; filename=TransactionDetail.xls");
            response.setHeader("Pragma" ,"no-cache");
            response.setHeader("Expires","0");

            Logger.info("A log mexxssage");
        System.out.print("successssdf");
        } catch (JRException e) {
            Logger.error("Error",e);
        }
    }
}
