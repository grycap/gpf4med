package org.grycap.gpf4med;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FilenameUtils.concat;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.grycap.gpf4med.concurrent.TaskStorage.TASK_STORAGE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.SizeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.grycap.gpf4med.concurrent.CancellableTask;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.threads.ImportReportsGroupTask;
import org.grycap.gpf4med.util.TRENCADISUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import trencadis.infrastructure.services.DICOMStorage.impl.wrapper.xmlOutputDownloadAllReportsID.DICOM_SR_ID;
import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.login.TRENCADIS_SESSION;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;

/**
 * Unit test for the XML binding classes supporting this application.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReportDownloadTasksTest {
	
	private static final File TEST_OUTPUT_DIR = new File(concat(getProperty("java.io.tmpdir"),
			ReportDownloadTasksTest.class.getSimpleName() + "_" + random(8, true, true)));
	
	private static final int PARTITION = 50;

	@Before
	public void setUp() {
		deleteQuietly(TEST_OUTPUT_DIR);
	}

	/**
	 * General tests of the report binding.
	 */
	@Test
	public void reportDownloadTest() {
		System.out.println("ReportDownloadTasksTest()");
		try {
			// create test dataset
			
			TRENCADIS_SESSION session = new TRENCADIS_SESSION(ConfigurationManager.INSTANCE.getTrencadisConfigFile(),
					ConfigurationManager.INSTANCE.getTrencadisPassword());
			TRENCADISUtils.getReportsID(session, 1, "5");
			
			Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorage = TRENCADISUtils.getDicomStorage();
			
			final ImportReportsGroupTask groupTask = new ImportReportsGroupTask();			
			if (dicomStorage != null) {
				for (TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorageIDS : dicomStorage) {
					final List<String> ids = new ArrayList<String>();
					final String centerName = dicomStorageIDS.getCenterName();
					final BackEnd backend = new BackEnd(dicomStorageIDS.getBackend().toString());
					for (DICOM_SR_ID id : dicomStorageIDS.getDICOM_DSR_IDS()) {
						ids.add(id.getValue());
					}
					groupTask.addTask(backend, centerName, session.getX509VOMSCredential(), ids, PARTITION, TEST_OUTPUT_DIR);
				}
				groupTask.sumbitAll();
				TASK_STORAGE.add(groupTask);
			}
			
			
			
			// test retrieving task from storage
			final CancellableTask<?> cancellableTask = TASK_STORAGE.get(groupTask.getUuid());
			assertThat("Cancellable task is not null", cancellableTask, notNullValue());
			assertThat("Type of cancellable task coincides with expected", cancellableTask instanceof ImportReportsGroupTask, equalTo(true));
			final ImportReportsGroupTask groupTask2 = (ImportReportsGroupTask) cancellableTask;

			// test task monitoring (using 2 minutes timeout)
			final Date timeout = new Date(new Date().getTime() + 120000l);
			do {
				System.err.println(" >> Progress: " + groupTask2.getProgress() + ", Status: " + groupTask2.getStatus());
				Thread.sleep(2000l);
			} while (new Date().compareTo(timeout) < 0 && !groupTask2.isDone());
			assertThat("Task has no errors", groupTask2.hasErrors(), equalTo(false));
			assertThat("Task is completed", groupTask2.isDone(), equalTo(true));

			// check downloaded files (is a file, is not empty and the filename coincides with the expected template)
			final List<IOFileFilter> fileFilters = newArrayList();
			fileFilters.add(FileFileFilter.FILE);
			fileFilters.add(new SizeFileFilter(10l));
			fileFilters.add(new RegexFileFilter("^.*trencadis-ds[\\d]+-[\\d]+\\.xml$"));
			final Collection<File> files = listFiles(TEST_OUTPUT_DIR,
					new AndFileFilter(fileFilters), TrueFileFilter.INSTANCE);
			assertThat("Downloaded files are not null", files, notNullValue());
			

		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("ReportDownloadTasksTest() failed: " + e.getMessage());
		} finally {			
			System.out.println("ReportDownloadTasksTest() has finished");
		}
	}

}