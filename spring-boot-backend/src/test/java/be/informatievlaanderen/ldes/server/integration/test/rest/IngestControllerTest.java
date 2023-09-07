package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.config.StreamsConfig;
import be.informatievlaanderen.ldes.server.integration.test.rest.converters.MemberConverter;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberDTO;
import com.apicatalog.jsonld.http.media.MediaType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@WebMvcTest
@ContextConfiguration(classes = {IngestController.class, MemberConverter.class, MemberConverter.class, StreamsConfig.class})
class IngestControllerTest {

    @MockBean
    private MemberGeometryService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StreamsConfig streamsConfig;

    @Test
    void when_MemberIsPosted_then_IngestMemberInService() throws Exception {
        Model model = RDFParser.source("members/mobility-hindrance.nq").lang(Lang.NQUADS).toModel();
        MemberDTO dto = new MemberDTO(model);
        MemberGeometry memberGeometry = dto.getMemberGeometry(streamsConfig.getStreams());

        mockMvc.perform(post("/members")
                        .content(readDataFromFile("members/mobility-hindrance.nq"))
                        .contentType(Lang.NQUADS.getHeaderString()))
                .andExpect(status().isOk());

        verify(service).ingestMemberGeometry(argThat(result -> result.getGeometry().equals(memberGeometry.getGeometry())));
    }

    private byte[] readDataFromFile(String filename) throws IOException {
        Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
        return Files.readAllBytes(path);
    }
}