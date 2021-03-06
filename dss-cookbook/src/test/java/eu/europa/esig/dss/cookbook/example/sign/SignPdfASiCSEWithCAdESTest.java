package eu.europa.esig.dss.cookbook.example.sign;

import org.junit.Test;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureForm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.asic.ASiCSignatureParameters;
import eu.europa.esig.dss.asic.signature.ASiCService;
import eu.europa.esig.dss.cookbook.example.CookbookTools;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class SignPdfASiCSEWithCAdESTest extends CookbookTools {

	@Test
	public void signASiCSBaselineB() {

		// GET document to be signed -
		// Return DSSDocument toSignDocument
		preparePdfDoc();

		// Get a token connection based on a pkcs12 file commonly used to store
		// private
		// keys with accompanying public key certificates, protected with a
		// password-based
		// symmetric key -
		// Return AbstractSignatureTokenConnection signingToken

		// and it's first private key entry from the PKCS12 store
		// Return DSSPrivateKeyEntry privateKey *****
		preparePKCS12TokenAndKey();

		// tag::demo[]

		// Preparing parameters for the AsicE signature
		ASiCSignatureParameters parameters = new ASiCSignatureParameters();
		
		// We choose the level of the signature (-B, -T, -LT or -LTA).
		parameters.setSignatureLevel(SignatureLevel.ASiC_E_BASELINE_B);
		// We choose CAdES as underlying form
		parameters.aSiC().setUnderlyingForm(SignatureForm.CAdES);
		
		// We set the digest algorithm to use with the signature algorithm. You
		// must use the
		// same parameter when you invoke the method sign on the token. The
		// default value is
		// SHA256
		parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);

		// We set the signing certificate
		parameters.setSigningCertificate(privateKey.getCertificate());
		// We set the certificate chain
		parameters.setCertificateChain(privateKey.getCertificateChain());

		// Create common certificate verifier
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
		// Create ASiCS service for signature
		ASiCService service = new ASiCService(commonCertificateVerifier);

		// Get the SignedInfo segment that need to be signed.
		ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);

		// This function obtains the signature value for signed information
		// using the
		// private key and specified algorithm
		DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
		SignatureValue signatureValue = signingToken.sign(dataToSign, digestAlgorithm, privateKey);

		// We invoke the xadesService to sign the document with the signature
		// value obtained in
		// the previous step.
		DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);

		// end::demo[]

		testFinalDocument(signedDocument);
	}

}
