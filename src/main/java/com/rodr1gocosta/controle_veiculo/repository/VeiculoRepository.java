package com.rodr1gocosta.controle_veiculo.repository;

import com.rodr1gocosta.controle_veiculo.dto.MarcaRelatorioResponse;
import com.rodr1gocosta.controle_veiculo.domain.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, UUID>, JpaSpecificationExecutor<Veiculo> {



    @Query("SELECT v FROM Veiculo v WHERE " +
           "(:marca IS NULL OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
           "(:ano IS NULL OR v.ano = :ano) AND " +
           "(:cor IS NULL OR LOWER(v.cor) LIKE LOWER(CONCAT('%', :cor, '%')))")
    Page<Veiculo> findByMarcaOrAnoOrCor(@Param("marca") String marca,
                                        @Param("ano") Integer ano,
                                        @Param("cor") String cor,
                                        Pageable pageable);

    @Query("SELECT v FROM Veiculo v WHERE " +
           "(:minPreco IS NULL OR v.preco >= :minPreco) AND " +
           "(:maxPreco IS NULL OR v.preco <= :maxPreco)")
    Page<Veiculo> findAllByPrecoBetween(@Param("minPreco") BigDecimal minPreco,
                                        @Param("maxPreco") BigDecimal maxPreco,
                                        Pageable pageable);

    @Query("SELECT new com.rodr1gocosta.controle_veiculo.dto.MarcaRelatorioResponse(v.marca, COUNT(v)) " +
            "FROM Veiculo v GROUP BY v.marca ORDER BY COUNT(v) DESC")
    Page<MarcaRelatorioResponse> countByMarca(Pageable pageable);
}
