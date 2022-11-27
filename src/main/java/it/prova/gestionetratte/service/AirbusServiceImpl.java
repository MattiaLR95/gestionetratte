package it.prova.gestionetratte.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.prova.gestionetratte.dto.AirbusDTO;
import it.prova.gestionetratte.dto.TrattaDTO;
import it.prova.gestionetratte.model.Airbus;
import it.prova.gestionetratte.repository.airbus.AirbusRepository;
import it.prova.gestionetratte.web.api.exception.AirbusNotFoundException;

@Service
public class AirbusServiceImpl implements AirbusService {

	@Autowired
	private AirbusRepository repository;

	@Override
	public List<Airbus> listAllElements() {
		return (List<Airbus>) repository.findAll();
	}

	@Override
	public List<Airbus> listAllElementsEager() {
		return (List<Airbus>) repository.findAllEager();
	}

	@Override
	public Airbus caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Airbus caricaSingoloElementoConTratte(Long id) {
		return repository.findByIdEager(id);
	}

	@Override
	@Transactional
	public Airbus aggiorna(Airbus airbusInstance) {
		return repository.save(airbusInstance);
	}

	@Override
	@Transactional
	public Airbus inserisciNuovo(Airbus airbusInstance) {
		return repository.save(airbusInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.findById(idToRemove)
				.orElseThrow(() -> new AirbusNotFoundException("Airbus not found con id: " + idToRemove));
		repository.deleteById(idToRemove);
	}

	@Override
	public List<Airbus> cercaByCodiceEDescrizioneILike(String codice, String descrizione) {
		return repository.findByDescrizioneIgnoreCaseContainingOrCodiceIgnoreCaseContainingOrderByCodiceAsc(codice,
				descrizione);
	}

	@Override
	public Airbus findByCodiceAndDescrizione(String codice, String descrizione) {
		return repository.findByCodiceAndDescrizione(codice, descrizione);
	}
	
	@Override
	public List<AirbusDTO> findListaAirbusEvidenziandoSovrapposizioni() {
		List<AirbusDTO> airbusConRelativeTratte = AirbusDTO.createRegistaDTOListFromModelList(repository.findAllEager(), true, false);
		for (AirbusDTO airbusItem : airbusConRelativeTratte) {
			for (TrattaDTO trattaItem : airbusItem.getTratte()) {
				for (TrattaDTO item : airbusItem.getTratte()) {
					if ((item.getData().isEqual(trattaItem.getData()) && item.getOraDecollo().isAfter(trattaItem.getOraDecollo())
							&& item.getOraDecollo().isBefore(trattaItem.getOraAtterraggio()))
							|| (item.getData().isEqual(trattaItem.getData()) && item.getOraAtterraggio().isAfter(trattaItem.getOraDecollo())
									&& item.getOraAtterraggio().isBefore(trattaItem.getOraAtterraggio()))) {
						airbusItem.setConSovrapposizioni(true);

					}
				}
			}
		}
		 airbusConRelativeTratte.stream().map(airbusEntity -> {
			airbusEntity.setTratte(null);
			return airbusEntity;
		}).collect(Collectors.toList());
		 
		 return airbusConRelativeTratte;

	}

}
