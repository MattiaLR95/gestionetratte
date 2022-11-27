package it.prova.gestionetratte.service;

import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.prova.gestionetratte.model.Stato;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.repository.tratta.TrattaRepository;
import it.prova.gestionetratte.web.api.exception.TrattaNotFoundException;
import it.prova.gestionetratte.web.api.exception.TratteAttiveNotFoundException;

@Service
public class TrattaServiceImpl implements TrattaService {

	@Autowired
	private TrattaRepository repository;

	@Override
	public List<Tratta> listAllElements(boolean eager) {
		if (eager)
			return (List<Tratta>) repository.findAllTrattaEager();

		return (List<Tratta>) repository.findAll();
	}

	@Override
	public Tratta caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Tratta caricaSingoloElementoEager(Long id) {
		return repository.findSingleTrattaEager(id);
	}

	@Override
	@Transactional
	public Tratta aggiorna(Tratta trattaInstance) {
		return repository.save(trattaInstance);
	}

	@Override
	public Tratta inserisciNuovo(Tratta trattaInstance) {
		return repository.save(trattaInstance);
	}

	@Override
	public void rimuovi(Long idToRemove) {
		repository.findById(idToRemove)
				.orElseThrow(() -> new TrattaNotFoundException("Film not found con id: " + idToRemove));
		repository.deleteById(idToRemove);
	}

	@Override
	public List<Tratta> findByCodiceAndDescrizione(String codice, String descrizione) {
		return repository.findByCodiceAndDescrizione(codice, descrizione);
	}

	@Override
	@Transactional
	public void concludiTratte() {
		List<Tratta> tratteAttive = repository.findAllByStatoLike(Stato.ATTIVA);

		if (tratteAttive.size() < 1)
			throw new TratteAttiveNotFoundException(
					"Errore. non sono presenti tratte attive! Impossibile completare l'operazione");

		tratteAttive.stream().filter(trattaItem -> LocalTime.now().isAfter(trattaItem.getOraAtterraggio()))
				.forEach(trattaItem -> trattaItem.setStato(Stato.CONCLUSA));
		tratteAttive.forEach(trattaItem -> repository.save(trattaItem));
	}

}
