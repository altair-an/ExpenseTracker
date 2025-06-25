// package com.altair.expensetracker.service;

// import com.altair.expensetracker.entity.Splits;
// import com.altair.expensetracker.repository.SplitsRepository;
// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class SplitsService {
//     private final SplitsRepository splitsRepository;
//     public SplitsService(SplitsRepository splitsRepository) {
//         this.splitsRepository = splitsRepository;
//     }

//     public List<Splits> getAllSplits() {
//         return splitsRepository.findAll();
//     }

//     public Splits getSplitsById(Long id) {
//         return splitsRepository.findById(id).orElse(null);
//     }

//     public Splits createSplits(Splits splits) {
//         return splitsRepository.save(splits);
//     }

//     public void deleteSplits(Long id) {
//         splitsRepository.deleteById(id);
//     }
// }
