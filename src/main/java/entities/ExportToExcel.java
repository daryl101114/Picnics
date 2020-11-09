package entities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExportToExcel extends QueryObject{

    public ExportToExcel() {

    }

    public void startExport(String report, LocalDate fromDate, LocalDate toDate){
        Object[][] data = null;
        List<String> headers = new ArrayList<>();
        int rowCount;
        int columnCount;
        int currRow = 0;
        int currColumn = 0;
        DateTimeFormatter formatterExtension = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String reportName = report + "_" + formatterExtension.format(LocalDateTime.now());


        switch (report){
            case "Accounting":{
                statement = "SELECT c.name, e.picnic_date_time, e.event_address, ii.item_desc, ii.item_quantity, ii.item_cost, ii.item_supplier_cost FROM invoice_item ii\n" +
                        "JOIN invoice i on ii.invoice_id = i.id AND i.is_paid = 1\n" +
                        "JOIN event e on i.id = e.invoice_id AND (e.picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "')\n" +
                        "JOIN customer c on e.customer_id = c.id\n" +
                        "ORDER BY e.picnic_date_time;";
                columnCount = 10;
                headers.add("Customer Name");
                headers.add("Picnic Date");
                headers.add("Address");
                headers.add("Item");
                headers.add("Quantity");
                headers.add("Unit Price");
                headers.add("Unit Cost");
                headers.add("Revenue");
                headers.add("Expense");
                headers.add("Profit");
                break;
            }
            case "Conversion Rate": {
                statement = "Conversion";
                columnCount = 0;
                headers.add("");
                headers.add("");
                headers.add("");
                break;
            }
            case "Best Locations":{
                statement = "Best";
                columnCount = 0;
                headers.add("");
                headers.add("");
                headers.add("");
                break;
            }
            case "Most Picnic Types":{
                statement = "Most";
                columnCount = 0;
                headers.add("");
                headers.add("");
                headers.add("");
                break;
            }
            case "Best Addons":{
                statement = "Addons";
                columnCount = 0;
                headers.add("");
                headers.add("");
                headers.add("");
                break;
            }
            default:
                columnCount = 0;
                headers.add("");
                headers.add("");
                headers.add("");
                break;
        }

        try {
            executeQuery(statement);
            if(resultSet != null){
                resultSet.last();
                rowCount = resultSet.getRow();
                resultSet.beforeFirst();
            } else return;

            data = new Object[rowCount + 1][columnCount];
            for(String string : headers){
                data[currRow][currColumn] = string;
                currColumn++;
            }
            currRow++;

            while(resultSet.next()) {
                switch (report) {
                    case "Accounting": {
                        data[currRow][0] = resultSet.getString("name");
                        data[currRow][1] = resultSet.getDate("picnic_date_time");
                        data[currRow][2] = resultSet.getString("event_address");
                        data[currRow][3] = resultSet.getString("item_desc");
                        int quantity = resultSet.getInt("item_quantity");
                        data[currRow][4] = quantity;
                        double cost = resultSet.getDouble("item_cost");
                        double expense = resultSet.getDouble("item_supplier_cost");
                        data[currRow][5] = cost;
                        data[currRow][6] = expense  * -1;
                        data[currRow][7] = quantity * cost;
                        data[currRow][8] = quantity * expense  * -1;
                        data[currRow][9] = (quantity * cost) + (quantity * expense);
                        break;
                    }

                    case "Conversion Rate":{
                        data[currRow][3] = resultSet.getString("column_name");
                        break;
                    }
                    case "Best Locations":{
                        data[currRow][4] = resultSet.getString("column_name");
                        break;
                    }
                    case "Most Picnic Types":{
                        data[currRow][5] = resultSet.getString("column_name");
                        break;
                    }
                    case "Best Addons":{
                        data[currRow][6] = resultSet.getString("column_name");
                        break;
                    }
                    default:
                        break;
                }
                currRow++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }

        export(data, report, reportName);
    }

    public void export(Object[][] data, String sheetName, String reportName){
        if(data == null)
            return;
        if(data.length < 1)
            return;

        int totalRows = data.length;
        int currColumn = 0;
        int currRow = 0;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        CellStyle cellStyle;

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(currRow);
        currRow++;
        for(Object header : data[0]){
            Cell cell = headerRow.createCell(currColumn);
            cell.setCellValue((String) header);
            cell.setCellStyle(headerStyle);
            currColumn++;
        }

        while(currRow < totalRows){
            currColumn = 0;
            Row row =  sheet.createRow(currRow);
            for(Object cellValue : data[currRow]){
                Cell cell = row.createCell(currColumn);
                cellStyle = workbook.createCellStyle();
                if(cellValue instanceof String){
                    cell.setCellValue((String) cellValue);
                }
                else if(cellValue instanceof Integer){
                    cell.setCellValue((Integer) cellValue);
                    cellStyle.setDataFormat((short)1);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Double){
                    cell.setCellValue((Double) cellValue);
                    cellStyle.setDataFormat((short)0x7);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Float){
                    cell.setCellValue((Float) cellValue);
                    cellStyle.setDataFormat((short)2);
                    cell.setCellStyle(cellStyle);
                }
                else if(cellValue instanceof Date){
                    cell.setCellValue((Date) cellValue);
                    cellStyle.setDataFormat((short)0xf);
                    cell.setCellStyle(cellStyle);
                }

                currColumn++;
            }
            currRow++;
        }

        try{
            String filePath = System.getProperty("user.home") + "/Downloads/" + reportName + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
