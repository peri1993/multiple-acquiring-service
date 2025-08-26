package co.id.yokke.multiacquiring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.id.yokke.multiacquiring.model.DataTempModel;

@Repository
public interface DataTempRepository extends JpaRepository<DataTempModel, String> {

}
