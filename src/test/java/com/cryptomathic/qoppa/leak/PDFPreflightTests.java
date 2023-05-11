package com.cryptomathic.qoppa.leak;

import com.qoppa.pdf.IPassword;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdfPreflight.PDFPreflight;
import com.qoppa.pdfPreflight.profiles.PDFAConversionOptions;
import com.qoppa.pdfPreflight.profiles.PDFA_2_B_Conversion;
import com.qoppa.pdfPreflight.results.PreflightResults;
import com.qoppa.pdfPreflight.results.ResultRecord;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class PDFPreflightTests {
  private final static long RETRY_NUMBER = 200;

  @Test
  void conversionLeadsToOutOfMemory() throws IOException, PDFException {
    boolean licenseStatus = PDFPreflight.setKey("insertYourLicenseHere");

    System.out.println("license: " + licenseStatus);

    for (int i = 1; i <= RETRY_NUMBER; i++) {
      try (InputStream is = PDFPreflight.class.getResourceAsStream("/4729b57f-92e1-413d-a386-290e0eadfe79-TICKET.hs_file_upload-test-doc_anonymisiert.pdf")) {
        PDFPreflight preflight = new PDFPreflight(is, (IPassword) null);

        PDFA_2_B_Conversion profile = new PDFA_2_B_Conversion();

        PDFAConversionOptions options = profile.getConversionOptions();
        options.setEmbeddedFiles(PDFAConversionOptions.OPTION_WARN);
        options.setTransparency(PDFAConversionOptions.OPTION_WARN);
        options.setUnsupportedAnnotations(PDFAConversionOptions.OPTION_WARN);

        PreflightResults results = preflight.convertDocument(profile, null);

        preflight.saveDocument(new FileOutputStream("/dev/null"));

        for(ResultRecord record: results.getResults()) {
          System.out.println("iteration: " + i + " line: " + record.getDetail());
        }

        preflight.close();
        preflight = null;
      }
    }
  }
}
