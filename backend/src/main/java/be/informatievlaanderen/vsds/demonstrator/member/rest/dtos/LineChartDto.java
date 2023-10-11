package be.informatievlaanderen.vsds.demonstrator.member.rest.dtos;

import java.util.List;

public class LineChartDto {
    private final List<String> labels;
    private final List<DataSetDto> dataSetDtos;

    public LineChartDto(List<String> labels, List<DataSetDto> dataSetDtos) {
        this.labels = labels;
        this.dataSetDtos = dataSetDtos;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<DataSetDto> getDataSetDtos() {
        return dataSetDtos;
    }
}
