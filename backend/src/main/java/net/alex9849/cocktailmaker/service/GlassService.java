package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Glass;
import net.alex9849.cocktailmaker.payload.dto.glass.GlassDto;
import net.alex9849.cocktailmaker.repository.GlassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class GlassService {

    @Autowired
    private GlassRepository glassRepository;

    public List<Glass> getAll() {
        return glassRepository.findByIds(glassRepository.findAllIds().toArray(Long[]::new));
    }

    public Glass getById(long id) {
        return glassRepository.findById(id).orElse(null);
    }

    public Glass createGlass(Glass glass) {
        if(!glassRepository.findIdsByName(glass.getName()).isEmpty()) {
            throw new IllegalArgumentException("A glass with the name " + glass.getName() + " already exists!");
        }
        return glassRepository.create(glass);
    }

    public Glass updateGlass(Glass glass) {
        Set<Long> idsWithName = glassRepository.findIdsByName(glass.getName());
        if(!idsWithName.isEmpty() && !idsWithName.contains(glass.getId())) {
            throw new IllegalArgumentException("A glass with the name " + glass.getName() + " already exists!");
        }
        glassRepository.update(glass);
        return glass;
    }

    public boolean deleteGlass(long id) {
        return glassRepository.delete(id);
    }

    public Glass getIngredientRecipeDefaultGlass() {
        Long siGlassId = glassRepository.getSingleIngredientGlassId();
        if(siGlassId == null) {
            return null;
        }
        return glassRepository.findById(siGlassId).orElse(null);
    }

    public Glass fromDto(GlassDto.Duplex.Detailed glassDto) {
        Glass glass = new Glass();
        glass.setSize(glassDto.getSize());
        glass.setName(glassDto.getName());
        glass.setId(glassDto.getId());
        glass.setUseForSingleIngredients(glassDto.isUseForSingleIngredients());
        glass.setDefault(glassDto.isDefault());
        return glass;
    }
}
