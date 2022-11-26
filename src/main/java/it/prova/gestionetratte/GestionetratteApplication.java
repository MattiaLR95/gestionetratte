package it.prova.gestionetratte;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.gestionetratte.model.Airbus;
import it.prova.gestionetratte.model.Stato;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.service.AirbusService;
import it.prova.gestionetratte.service.TrattaService;

@SpringBootApplication
public class GestionetratteApplication implements CommandLineRunner {

	@Autowired
	private AirbusService airbusService;

	@Autowired
	private TrattaService trattaService;

	public static void main(String[] args) {
		SpringApplication.run(GestionetratteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String codice = "PROVA1";
		String descrizione = "Roma - Catania";
		Integer numeroPassegeri = 100;

		DateTimeFormatter formatterDataAirbus = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String dateAirbus = "10/12/2022";
		LocalDate localDateAirbus = LocalDate.parse(dateAirbus, formatterDataAirbus);

		Airbus airbusNew = airbusService.findByCodiceAndDescrizione(codice, descrizione);

		if (airbusNew == null) {
			airbusNew = new Airbus(1L, codice, descrizione, localDateAirbus, numeroPassegeri);
			airbusService.inserisciNuovo(airbusNew);
		}

		DateTimeFormatter formatterDataTrattaRoma = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String datePartenzaRoma = "23/12/2022";
		LocalDate localDate = LocalDate.parse(datePartenzaRoma, formatterDataTrattaRoma);

		DateTimeFormatter formatterOraDecollo = DateTimeFormatter.ofPattern("HH.mm");
		String time = "20.00";
		LocalTime localTimeObjDecollo = LocalTime.parse(time, formatterOraDecollo);

		DateTimeFormatter formatterOraArrivo = DateTimeFormatter.ofPattern("HH.mm");
		String time1 = "06.00";
		LocalTime localTimeObjArrivo = LocalTime.parse(time1, formatterOraArrivo);

		Tratta tratta = new Tratta(1L, "PROVA1", "Roma - Catania", localDate, localTimeObjDecollo, localTimeObjArrivo,
				Stato.ATTIVA, airbusNew);
		if (trattaService.findByCodiceAndDescrizione(tratta.getCodice(), tratta.getDescrizione()).isEmpty())
			trattaService.inserisciNuovo(tratta);
	}
}
