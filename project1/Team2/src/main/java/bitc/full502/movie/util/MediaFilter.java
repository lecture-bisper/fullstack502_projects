package bitc.full502.movie.util;

import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.SearchDTO;

import java.text.Collator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MediaFilter {

    public static List<MediaDTO> filterMedia(List<MediaDTO> mediaList, SearchDTO searchDTO) {

        List<String> selectedGenres = Optional.ofNullable(searchDTO.getGenre())
                .map(g -> Arrays.stream(g.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        LocalDate startDate = searchDTO.getStartDate();
        LocalDate endDate = searchDTO.getEndDate();

        // 날짜 역전 방지
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        final LocalDate sDate = startDate;
        final LocalDate eDate = endDate;

        List<MediaDTO> filteredList = mediaList.stream()
                .filter(media -> {
                    LocalDate releaseDate = media.getReleaseDate();
                    boolean dateMatch = true;

                    if (sDate != null && eDate != null) {
                        // 둘 다 있을 때 범위 필터
                        if (releaseDate == null) return false;
                        dateMatch = !releaseDate.isBefore(sDate) && !releaseDate.isAfter(eDate);

                    } else if (sDate != null) {
                        // startDate만 있을 때
                        if (releaseDate == null) return false;
                        dateMatch = !releaseDate.isBefore(sDate);

                    } else if (eDate != null) {
                        // endDate만 있을 때
                        if (releaseDate == null) return false;
                        dateMatch = !releaseDate.isAfter(eDate);

                    } // 둘 다 없으면 항상 true

                    // 장르 필터
                    boolean genreMatch = true;
                    if (!selectedGenres.isEmpty()) {
                        if (media.getGenreIds() == null) return false;
                        Set<String> mediaGenres = Arrays.stream(media.getGenreIds().split(","))
                                .map(String::trim)
                                .collect(Collectors.toSet());
                        genreMatch = mediaGenres.containsAll(selectedGenres);
                    }

                    boolean countryMatch = true;
                    String searchCountry = searchDTO.getCountry();
                    if (searchCountry != null && !searchCountry.trim().isEmpty()) {
                        if (media.getOriginCountry() == null) return false;
                        countryMatch = media.getOriginCountry().equalsIgnoreCase(searchCountry.trim());
                    }

                    return dateMatch && genreMatch && countryMatch;
                })
                .collect(Collectors.toList());

        String sort = searchDTO.getSort();
        if (sort != null && !sort.equals("noneSort")) {
            Comparator<MediaDTO> comparator = null;

            switch (sort) {
                case "voteAsc":
                    comparator = Comparator.comparing(MediaDTO::getVoteAverage, Comparator.nullsLast(Double::compareTo));
                    break;
                case "voteDesc":
                    comparator = Comparator.comparing(MediaDTO::getVoteAverage, Comparator.nullsLast(Double::compareTo)).reversed();
                    break;
                case "titleAsc":
                    Collator collatorAsc = Collator.getInstance(Locale.KOREAN);
                    collatorAsc.setStrength(Collator.PRIMARY);
                    Comparator<String> collatorComparatorAsc = (s1, s2) -> {
                        if (s1 == null && s2 == null) return 0;
                        if (s1 == null) return 1;
                        if (s2 == null) return -1;
                        return collatorAsc.compare(s1, s2);
                    };
                    comparator = Comparator.comparing(MediaDTO::getTitle, collatorComparatorAsc);
                    break;

                case "titleDesc":
                    Collator collatorDesc = Collator.getInstance(Locale.KOREAN);
                    collatorDesc.setStrength(Collator.PRIMARY);
                    Comparator<String> collatorComparatorDesc = (s1, s2) -> {
                        if (s1 == null && s2 == null) return 0;
                        if (s1 == null) return 1;
                        if (s2 == null) return -1;
                        return collatorDesc.compare(s1, s2);
                    };
                    comparator = Comparator.comparing(MediaDTO::getTitle, collatorComparatorDesc).reversed();
                    break;
                case "dateAsc":
                    comparator = Comparator.comparing(MediaDTO::getReleaseDate, Comparator.nullsLast(LocalDate::compareTo));
                    break;
                case "dateDesc":
                    comparator = Comparator.comparing(MediaDTO::getReleaseDate, Comparator.nullsLast(LocalDate::compareTo)).reversed();
                    break;
            }

            if (comparator != null) {
                filteredList.sort(comparator);
            }
        }

        return filteredList;
    }
}
