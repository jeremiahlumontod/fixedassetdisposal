package org.jml.fixedassetdisposal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BpmRepository extends JpaRepository<ProcessDetails, Long> {

}