package roofsense.ui;

import com.google.inject.Guice;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import roofsense.adapters.ui.RoofForm;
import roofsense.config.RoofSenseModule;
import roofsense.entities.Roof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for the CreateRoofNode view.
 * It tests the full flow from the GUI to the database persistence.
 */
@ExtendWith(ApplicationExtension.class)
class RoofFormIntegrationTest {

    @SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    private EntityManagerFactory emf;

    /**
     * TestFX calls this method to start the JavaFX application.
     *
     * @param stage The primary stage for this application.
     */
    @Start
    public void start(final Stage stage) {
        final var injector = Guice.createInjector(new RoofSenseModule());

        this.emf = injector.getInstance(EntityManagerFactory.class);

        final var view = injector.getInstance(RoofForm.class);
        stage.setScene(new Scene((Parent) view.getRootNode()));
        stage.show();
    }

    /**
     * Cleans up the database after each test by removing all roofs.
     */
    @AfterEach
    void tearDown() {
        if (this.emf != null && this.emf.isOpen()) {
            final EntityManager em = this.emf.createEntityManager();
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Roof").executeUpdate();
            em.getTransaction().commit();
            em.close();
            this.emf.close();
        }
    }

    @Test
    void createNewRoofGoesWellTest(final FxRobot robot) {
        // given
        final var roofCode = "R-01";
        final var roofBuildingAddress = "Main Street 1";

        // when
        robot.clickOn("#codeTextField").write(roofCode);
        robot.clickOn("#buildingAddressTextField").write(roofBuildingAddress);
        robot.clickOn("#saveButton");

        // then
        FxAssert.verifyThat("#saveResultLabel", LabeledMatchers.hasText("Roof created successfully"));
        final EntityManager em = this.emf.createEntityManager();
        final var persistedRoof = em.createQuery("FROM Roof WHERE code = :code", Roof.class)
                .setParameter("code", roofCode)
                .getSingleResult();
        em.close();

        assertNotNull(persistedRoof);
        assertEquals(roofCode, persistedRoof.getCode());
        assertEquals(roofBuildingAddress, persistedRoof.getBuildingAddress());
    }

}
