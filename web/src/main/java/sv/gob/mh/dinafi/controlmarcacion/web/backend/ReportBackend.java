/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlViewMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.service.ViewMarcacionService;

@ViewScoped
@Named
@Getter
@Setter
@Log
public class ReportBackend implements Serializable {

    private static final long serialVersionUID = -7862968709824531223L;

    @Inject
    private ViewMarcacionService viewMarcacionService;

    private List<CtrlViewMarcacion> marcaciones;
    private Date desde = new Date();
    private Date hasta = new Date();

    @PostConstruct
    public void init() {
//        hasta = new Date(hasta.getTime() + 10000);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        desde = cal.getTime();
        hasta = desde;
        findMarcaciones();
    }

    public List<CtrlViewMarcacion> findMarcaciones() {
        marcaciones = new ArrayList<>();
        if (desde != null && hasta != null) {
            Instant current = desde.toInstant();
            LocalDateTime ldtDesde = LocalDateTime.ofInstant(current, ZoneId.systemDefault());
            current = hasta.toInstant();
            LocalDateTime ldtHasta = LocalDateTime.ofInstant(current, ZoneId.systemDefault());
            marcaciones = viewMarcacionService.findMarcacionesByDates(ldtDesde, ldtHasta);
        }
        return marcaciones;
    }

    public void postProcessXLS(Object document) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDesde = dateFormat.format(desde);
        String strHasta = dateFormat.format(hasta);
        HSSFWorkbook wb = (HSSFWorkbook) document;
        wb.setSheetName(0, "del ".concat(strDesde).concat(" al ").concat(strHasta));

//        HSSFSheet sheet = wb.getSheetAt(0);
//        final Font font = sheet.getWorkbook().createFont();
//        font.setFontName("Arial");
//        font.setBold(true);
//        
//    
//        final CellStyle style = sheet.getWorkbook().createCellStyle();
//        style.setFont(font);
//
//        Row header = sheet.createRow(0);
//        header.createCell(0).setCellValue("Marcaciones del xxxx al   xxxxxx");
//        header.setRowStyle(style);
//        header.getRowStyle().setFont(font);
    }

}
