package co.topl.latticedamldemo.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface OrganizationRepository extends CrudRepository<Organization, String> {

}
