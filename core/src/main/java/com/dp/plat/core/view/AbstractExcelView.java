/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dp.plat.core.view;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

/**
 * 	
 * Convenient superclass for Excel document views.
 * Compatible with Apache POI 3.5 and higher, as of Spring 4.0.
 *
 * <p>Properties:
 * <ul>
 * <li>url (optional): The url of an existing Excel document to pick as a starting point.
 * It is done without localization part nor the ".xlsx" extension.
 * </ul>
 *
 * <p>The file will be searched with locations in the following order:
 * <ul>
 * <li>[url]_[language]_[country].xlsx
 * <li>[url]_[language].xlsx
 * <li>[url].xlsx
 * </ul>
 *
 * <p>For working with the workbook in the subclass, see
 * <a href="http://jakarta.apache.org/poi/index.html">Jakarta's POI site</a>
 *
 * <p>As an example, you can try this snippet:
 *
 * <pre class="code">
 * protected void buildExcelDocument(
 *     Map&lt;String, Object&gt; model, Workbook workbook,
 *     HttpServletRequest request, HttpServletResponse response) {
 *
 *   // Go to the first sheet.
 *   // getSheetAt: only if workbook is created from an existing document
 * 	 // Sheet sheet = workbook.getSheetAt(0);
 * 	 Sheet sheet = workbook.createSheet("Spring");
 * 	 sheet.setDefaultColumnWidth(12);
 *
 *   // Write a text at A1.
 *   Cell cell = getCell(sheet, 0, 0);
 *   setText(cell, "Spring POI test");
 *
 *   // Write the current date at A2.
 *   CellStyle dateStyle = workbook.createCellStyle();
 *   dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
 *   cell = getCell(sheet, 1, 0);
 *   cell.setCellValue(new Date());
 *   cell.setCellStyle(dateStyle);
 *
 *   // Write a number at A3
 *   getCell(sheet, 2, 0).setCellValue(458);
 *
 *   // Write a range of numbers.
 *   Row sheetRow = sheet.createRow(3);
 *   for (short i = 0; i < 10; i++) {
 *     sheetRow.createCell(i).setCellValue(i * 10);
 *   }
 * }</pre>
 *
 * This class is similar to the AbstractExcelView class in usage style.
 *
 * @author w02611
 * 
 * @see  org.springframework.web.servlet.view.document.AbstractExcelView
 * 
 */
public abstract class AbstractExcelView extends AbstractView {

	/** The content type for an Excel response */
	private static final String DEFAULT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	/** The extension to look for existing templates */
	private static final String DEFAULT_EXTENSION = ".xlsx";

	/** The rowAccessWindowSize to saved in memory */
	private static final int rowAccessWindowSize = 10000;

	private String url;
	
	private String extension = DEFAULT_EXTENSION;


	/**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".
	 */
	public AbstractExcelView() {
		setContentType(DEFAULT_CONTENT_TYPE);
	}

	/**
	 * Set the URL of the Excel workbook source, without localization part nor extension.
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Renders the Excel view, given the specified model.
	 */
	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		SXSSFWorkbook workbook;
		ByteArrayOutputStream baos = createTemporaryOutputStream(); 
		if (this.url != null) {
			workbook = getTemplateSource(this.url, request);
		}
		else {
			workbook = new SXSSFWorkbook(rowAccessWindowSize);
			logger.debug("Created Excel Workbook from scratch");
		}
		workbook.setCompressTempFiles(true);
		buildExcelDocument(model, workbook, request, response);

		// Set the content type.
		response.setContentType(getContentType());

		// Should we set the content length here?
		// response.setContentLength(workbook.getBytes().length);
		
		// Flush byte array to servlet output stream.
//		ServletOutputStream out = response.getOutputStream();
		workbook.write(baos);
		writeToResponse(response, baos);
//		out.flush();
	}

	/**
	 * Creates the workbook from an existing XLSX document.
	 * @param url the URL of the Excel template without localization part nor extension
	 * @param request current HTTP request
	 * @return the XSSFWorkbook
	 * @throws Exception in case of failure
	 */
	protected SXSSFWorkbook getTemplateSource(String url, HttpServletRequest request) throws Exception {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(url, extension, userLocale);

		// Create the Excel document from the source.
		if (logger.isDebugEnabled()) {
			logger.debug("Loading Excel workbook from " + inputFile);
		}
//		POIFSFileSystem fs = new POIFSFileSystem(inputFile.getInputStream());
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputFile.getInputStream());
		return new SXSSFWorkbook(xssfWorkbook, rowAccessWindowSize);
	}

	/**
	 * Subclasses must implement this method to create an Excel Workbook document,
	 * given the model.
	 * @param model the model Map
	 * @param workbook the Excel workbook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(
			Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception;


	/**
	 * Convenient method to obtain the cell in the given sheet, row and column.
	 * <p>Creates the row and the cell if they still doesn't already exist.
	 * Thus, the column can be passed as an int, the method making the needed downcasts.
	 * @param sheet a sheet object. The first sheet is usually obtained by workbook.getSheetAt(0)
	 * @param row thr row number
	 * @param col the column number
	 * @return the Cell
	 */
	protected Cell getCell(Sheet sheet, int row, int col) {
		Row sheetRow = sheet.getRow(row);
		if (sheetRow == null) {
			sheetRow = sheet.createRow(row);
		}
		Cell cell = sheetRow.getCell(col);
		if (cell == null) {
			cell = sheetRow.createCell(col);
		}
		return cell;
	}

	/**
	 * Convenient method to set a String as text content in a cell.
	 * @param cell the cell in which the text must be put
	 * @param text the text to put in the cell
	 */
	protected void setText(Cell cell, String text) {
//		cell.setCellType(Cell.);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(text);
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public static int getRowaccesswindowsize() {
		return rowAccessWindowSize;
	}

	public String getUrl() {
		return url;
	}
	
}
