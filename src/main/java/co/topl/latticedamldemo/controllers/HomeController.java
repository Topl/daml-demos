package co.topl.latticedamldemo.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.daml.ledger.api.v1.CommandSubmissionServiceGrpc.CommandSubmissionServiceBlockingStub;
import com.daml.ledger.api.v1.CommandSubmissionServiceOuterClass.SubmitRequest;
import com.daml.ledger.api.v1.CommandsOuterClass.Command;
import com.daml.ledger.api.v1.CommandsOuterClass.Commands;
import com.daml.ledger.api.v1.CommandsOuterClass.ExerciseByKeyCommand;
import com.daml.ledger.api.v1.CommandsOuterClass.ExerciseCommand;
import com.daml.ledger.api.v1.ValueOuterClass.Identifier;
import com.daml.ledger.api.v1.ValueOuterClass.Identifier.Builder;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.LedgerOffset;
import com.daml.ledger.javaapi.data.NoFilter;
import com.daml.ledger.javaapi.data.Transaction;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.api.v1.ValueOuterClass.Record;
import com.daml.ledger.api.v1.ValueOuterClass.RecordField;
import com.daml.ledger.api.v1.ValueOuterClass.Value;

import co.topl.daml.DamlAppContext;
import co.topl.daml.ToplContext;
import co.topl.daml.assets.processors.SignedAssetTransferRequestProcessor;
import co.topl.daml.assets.processors.SignedMintingRequestProcessor;
import co.topl.daml.operator.AssetIouProcessor;
import co.topl.latticedamldemo.LatticeDamlDemoApplication;
import co.topl.latticedamldemo.configuration.DemoConfiguration;
import co.topl.latticedamldemo.dtos.AddAssetCodeDto;
import co.topl.latticedamldemo.dtos.MintOrUpdateAssetDto;
import co.topl.latticedamldemo.model.AssetCode;
import co.topl.latticedamldemo.model.AssetCodeInventoryEntry;
import co.topl.latticedamldemo.model.Member;
import co.topl.latticedamldemo.model.MembersRepository;
import co.topl.latticedamldemo.model.Organization;
import co.topl.latticedamldemo.model.OrganizationRepository;
import io.reactivex.Flowable;

import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.Event;

@Controller
@RequestMapping("/home")
public class HomeController {

        @Autowired
        DemoConfiguration demoConfiguration;

        @Autowired
        private OrganizationRepository organizationRepository;

        @Autowired
        private MembersRepository memberRepository;

        @Autowired
        CommandSubmissionServiceBlockingStub commandSubmissionService;

        @Autowired
        DamlAppContext damlAppContext;

        @Autowired
        ToplContext toplContext;

        @Autowired
        DamlLedgerClient client;

        @Autowired
        SessionFactory sessionFactory;

        @Autowired
        Flowable<Transaction> transactions;

        @GetMapping
        public String getHomePage(Model model) {
                return "user/home";
        }

        @GetMapping("/organizations")
        public String getOrgs(Model model) {
                List<Organization> organizations = new ArrayList<>();
                for (Organization o : organizationRepository.findAll()) {
                        organizations.add(o);
                }
                model.addAttribute("organizations", organizations);
                return "user/organizationsUsers";
        }

        @GetMapping("/organization/{orgName}/assets")
        public String getAssets(@PathVariable String orgName, Model model) {
                Optional<Organization> someOrganization = organizationRepository.findById(orgName);
                model.addAttribute("orgName", orgName);
                model.addAttribute("assets", someOrganization.get().getAssetCodes());
                return "user/assets";
        }

        @GetMapping("/organization/{orgName}/assetsInventory/{assetId}")
        public String getAssetInventory(@PathVariable String orgName, @PathVariable Long assetId, Model model) {
                Optional<Organization> someOrganization = organizationRepository.findById(orgName);
                model.addAttribute("orgName", orgName);
                model.addAttribute("assetId", assetId);
                AssetCode assetCode = null;
                for (AssetCode code : someOrganization.get().getAssetCodes()) {
                        if (code.getId().equals(assetId)) {
                                assetCode = code;
                                break;
                        }
                }
                model.addAttribute("assets", assetCode.getAssetCodeInventoryEntry().stream()
                                .map(p -> {
                                        p.setContractId(p.getContractId().substring(0, 5) + "...");
                                        return p;
                                }).collect(Collectors.toList()));
                return "user/assetsInventory";
        }

        @GetMapping("/organization/{orgName}/mintAsset/{assetId}")
        public String getMintAsset(@PathVariable String orgName, @PathVariable Long assetId, Model model) {
                Optional<Organization> someOrganization = organizationRepository.findById(orgName);
                model.addAttribute("orgName", orgName);
                model.addAttribute("assetId", assetId);
                AssetCode assetCode = null;
                for (AssetCode code : someOrganization.get().getAssetCodes()) {
                        if (code.getId().equals(assetId)) {
                                assetCode = code;
                                break;
                        }
                }
                MintOrUpdateAssetDto mintAssetDto = new MintOrUpdateAssetDto();
                mintAssetDto.setOrgName(orgName);
                mintAssetDto.setAssetId(assetId);
                mintAssetDto.setShortName(assetCode.getShortName());
                model.addAttribute("mintAssetDto", mintAssetDto);
                return "user/mintAsset";
        }

        @GetMapping("/organization/{orgName}/updateAsset/{assetId}/{inventoryId}")
        public String getUpdateAssetInventory(@PathVariable String orgName, @PathVariable Long assetId,
                        @PathVariable String inventoryId, Model model) {
                Optional<Organization> someOrganization = organizationRepository.findById(orgName);
                model.addAttribute("orgName", orgName);
                model.addAttribute("inventoryId", inventoryId);
                model.addAttribute("assetId", assetId);
                AssetCode assetCode = null;
                for (AssetCode code : someOrganization.get().getAssetCodes()) {
                        if (code.getId().equals(assetId)) {
                                assetCode = code;
                                break;
                        }
                }
                AssetCodeInventoryEntry assetCodeInventoryEntry = null;
                for (AssetCodeInventoryEntry entry : assetCode.getAssetCodeInventoryEntry()) {
                        if (entry.getIouIdentifier().equals(inventoryId)) {
                                assetCodeInventoryEntry = entry;
                                break;
                        }
                }
                MintOrUpdateAssetDto updateAssetDto = new MintOrUpdateAssetDto();
                updateAssetDto.setOrgName(orgName);
                updateAssetDto.setAssetId(assetId);
                updateAssetDto.setIouIdentifier(assetCodeInventoryEntry.getIouIdentifier());
                updateAssetDto.setShortName(assetCode.getShortName());
                updateAssetDto.setContractId(assetCodeInventoryEntry.getContractId());
                model.addAttribute("updateAssetDto", updateAssetDto);
                return "user/updateAsset";
        }

        @PostMapping("/organization/mintAsset")
        public RedirectView mintAssetSubmit(Principal principal,
                        @ModelAttribute MintOrUpdateAssetDto mintAssetDto, Model model) {
                Builder assetCreatorIdentifier = Identifier.newBuilder()
                                .setPackageId(co.topl.daml.api.model.topl.organization.AssetCreator.TEMPLATE_ID
                                                .getPackageId())
                                .setEntityName(co.topl.daml.api.model.topl.organization.AssetCreator.TEMPLATE_ID
                                                .getEntityName())
                                .setModuleName(co.topl.daml.api.model.topl.organization.AssetCreator.TEMPLATE_ID
                                                .getModuleName());
                Optional<Member> someUser = memberRepository.findById(principal.getName());
                Optional<Member> someOperator = memberRepository.findById(demoConfiguration.getOperatorId());
                SignedMintingRequestProcessor signedMintingRequestProcessor = new SignedMintingRequestProcessor(
                                damlAppContext,
                                toplContext,
                                () -> java.util.UUID.randomUUID().toString());
                transactions.forEachWhile(signedMintingRequestProcessor::processTransaction);
                Command exerciseCommand = Command.newBuilder().setExerciseByKey(
                                ExerciseByKeyCommand.newBuilder()
                                                .setTemplateId(assetCreatorIdentifier.build())
                                                .setContractKey(Value.newBuilder()
                                                                .setRecord(Record.newBuilder()
                                                                                .addFields(0, RecordField.newBuilder()
                                                                                                .setLabel("_1")
                                                                                                .setValue(
                                                                                                                Value.newBuilder()
                                                                                                                                .setParty(someOperator
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier())))
                                                                                .addFields(1, RecordField.newBuilder()
                                                                                                .setLabel("_2")
                                                                                                .setValue(
                                                                                                                Value.newBuilder()
                                                                                                                                .setText(mintAssetDto
                                                                                                                                                .getOrgName())))
                                                                                .addFields(2, RecordField.newBuilder()
                                                                                                .setLabel("_3")
                                                                                                .setValue(
                                                                                                                Value.newBuilder()
                                                                                                                                .setRecord(
                                                                                                                                                Record.newBuilder()
                                                                                                                                                                .addFields(0,
                                                                                                                                                                                RecordField.newBuilder()
                                                                                                                                                                                                .setValue(
                                                                                                                                                                                                                Value.newBuilder()
                                                                                                                                                                                                                                .setInt64(1L)))
                                                                                                                                                                .addFields(1,
                                                                                                                                                                                RecordField.newBuilder()
                                                                                                                                                                                                .setValue(
                                                                                                                                                                                                                Value.newBuilder()
                                                                                                                                                                                                                                .setText(
                                                                                                                                                                                                                                                mintAssetDto.getShortName())))
                                                                                                                                                                .build())))))
                                                .setChoice("MintAsset")
                                                .setChoiceArgument(
                                                                Value.newBuilder()
                                                                                .setRecord(Record
                                                                                                .newBuilder()
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("requestor")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setParty(someUser
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("quantity")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setInt64(mintAssetDto
                                                                                                                                                .getQuantity())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("someCommitRoot")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .build())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("someMetadata")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                                                .setText(mintAssetDto
                                                                                                                                                                                .getMetadata()))
                                                                                                                                                .build())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("someFee")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .build()))))
                                                                                .build()))
                                .build();
                SubmitRequest commandSubmitRequest = SubmitRequest.newBuilder()
                                .setCommands(
                                                Commands.newBuilder()
                                                                .setCommandId(UUID.randomUUID().toString())
                                                                .setParty(someUser.get().getPartyIdentifier())
                                                                .setApplicationId(demoConfiguration.getAppId())
                                                                .addCommands(exerciseCommand)
                                                                .build())
                                .build();

                commandSubmissionService.submit(commandSubmitRequest);

                RedirectView redirectView = new RedirectView();
                redirectView
                                .setUrl("/home/organization/" + mintAssetDto.getOrgName() + "/assetsInventory/"
                                                + mintAssetDto.getAssetId());
                return redirectView;
        }

        @PostMapping("/organization/updateAssetInventory")
        public RedirectView updateAssetSubmit(Principal principal,
                        @ModelAttribute MintOrUpdateAssetDto updateAssetDto, Model model) {
                Builder assetIouIdentifier = Identifier.newBuilder()
                                .setPackageId(co.topl.daml.api.model.topl.organization.AssetIou.TEMPLATE_ID
                                                .getPackageId())
                                .setEntityName(co.topl.daml.api.model.topl.organization.AssetIou.TEMPLATE_ID
                                                .getEntityName())
                                .setModuleName(co.topl.daml.api.model.topl.organization.AssetIou.TEMPLATE_ID
                                                .getModuleName());
                Optional<Member> someUser = memberRepository.findById(principal.getName());
                Organization org = organizationRepository.findById(updateAssetDto.getOrgName()).get();
                AssetCode assetCode = null;
                for (AssetCode code : org.getAssetCodes()) {
                        if (code.getId().equals(updateAssetDto.getAssetId())) {
                                assetCode = code;
                                break;
                        }
                }
                AssetCodeInventoryEntry assetCodeInventoryEntry = null;
                for (AssetCodeInventoryEntry entry : assetCode.getAssetCodeInventoryEntry()) {
                        if (entry.getIouIdentifier().equals(updateAssetDto.getIouIdentifier())) {
                                assetCodeInventoryEntry = entry;
                                // remove the old inventory entry
                                assetCode.getAssetCodeInventoryEntry().remove(entry);
                                break;
                        }
                }
                organizationRepository.save(org);
                String newMetadata = updateAssetDto.getMetadata();
                if (newMetadata.trim().isEmpty()) {
                        newMetadata = assetCodeInventoryEntry.getMetadata();
                }
                final String iouIdentifier = assetCodeInventoryEntry.getIouIdentifier();
                SignedAssetTransferRequestProcessor signedTransferRequestProcessor = new SignedAssetTransferRequestProcessor(
                                damlAppContext,
                                toplContext,
                                () -> iouIdentifier);
                transactions.forEachWhile(signedTransferRequestProcessor::processTransaction);
                Command exerciseCommand = Command.newBuilder().setExercise(
                                ExerciseCommand.newBuilder()
                                                .setTemplateId(assetIouIdentifier.build())
                                                .setContractId(updateAssetDto.getContractId())
                                                .setChoice("AssetIou_UpdateAsset")
                                                .setChoiceArgument(
                                                                Value.newBuilder()
                                                                                .setRecord(Record
                                                                                                .newBuilder()
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("requestor")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setParty(someUser
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("newCommitRoot")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .build())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("newMetadata")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                                                .setText(updateAssetDto
                                                                                                                                                                                .getMetadata()))
                                                                                                                                                .build())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("someFee")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setOptional(Value
                                                                                                                                                .newBuilder()
                                                                                                                                                .getOptionalBuilder()
                                                                                                                                                .build()))))
                                                                                .build()))
                                .build();
                SubmitRequest commandSubmitRequest = SubmitRequest.newBuilder()
                                .setCommands(
                                                Commands.newBuilder()
                                                                .setCommandId(UUID.randomUUID().toString())
                                                                .setParty(someUser.get().getPartyIdentifier())
                                                                .setApplicationId(demoConfiguration.getAppId())
                                                                .addCommands(exerciseCommand)
                                                                .build())
                                .build();

                commandSubmissionService.submit(commandSubmitRequest);

                RedirectView redirectView = new RedirectView();
                redirectView
                                .setUrl("/home/organization/" + updateAssetDto.getOrgName() + "/assetsInventory/"
                                                + updateAssetDto.getAssetId());
                return redirectView;
        }

        @GetMapping("/organization/{orgName}/addAsset")
        public String getAddOrganizationPage(@PathVariable String orgName, Model model) {
                model.addAttribute("orgName", orgName);
                AddAssetCodeDto assetCodeDto = new AddAssetCodeDto();
                assetCodeDto.setOrgName(orgName);
                model.addAttribute("addAssetCodeDto", assetCodeDto);

                return "user/addAsset";
        }

        @PostMapping("/organization/addAsset")
        public RedirectView getAddAssetSubmit(Principal principal,
                        @ModelAttribute AddAssetCodeDto addAssetCodeDto, Model model) {
                Builder organizationIdentifier = Identifier.newBuilder()
                                .setPackageId(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getPackageId())
                                .setEntityName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getEntityName())
                                .setModuleName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getModuleName());

                Optional<Member> someUser = memberRepository.findById(principal.getName());
                Optional<Member> someOperator = memberRepository.findById(demoConfiguration.getOperatorId());

                Command exerciseCommand = Command.newBuilder().setExerciseByKey(
                                ExerciseByKeyCommand.newBuilder()
                                                .setTemplateId(organizationIdentifier.build())
                                                .setContractKey(Value.newBuilder()
                                                                .setRecord(Record.newBuilder()
                                                                                .addFields(0, RecordField.newBuilder()
                                                                                                .setLabel("_1")
                                                                                                .setValue(
                                                                                                                Value.newBuilder()
                                                                                                                                .setParty(someOperator
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier())))
                                                                                .addFields(1, RecordField.newBuilder()
                                                                                                .setLabel("_2")
                                                                                                .setValue(
                                                                                                                Value.newBuilder()
                                                                                                                                .setText(addAssetCodeDto
                                                                                                                                                .getOrgName())))))
                                                .setChoice("Organization_CreateAsset")
                                                .setChoiceArgument(
                                                                Value.newBuilder()
                                                                                .setRecord(Record
                                                                                                .newBuilder()
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("requestor")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setParty(someUser
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier())))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("version")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setInt64(1L)))
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("shortName")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setText(addAssetCodeDto
                                                                                                                                                .getShortName()))))
                                                                                .build()))
                                .build();

                SubmitRequest commandSubmitRequest = SubmitRequest.newBuilder()
                                .setCommands(
                                                Commands.newBuilder()
                                                                .setCommandId(UUID.randomUUID().toString())
                                                                .setParty(someUser.get().getPartyIdentifier())
                                                                .setApplicationId(demoConfiguration.getAppId())
                                                                .addCommands(exerciseCommand)
                                                                .build())
                                .build();

                commandSubmissionService.submit(commandSubmitRequest);
                Optional<Organization> someOrg = organizationRepository.findById(addAssetCodeDto.getOrgName());
                Organization org = someOrg.get();
                AssetCode assetCode = new AssetCode();
                assetCode.setShortName(addAssetCodeDto.getShortName());
                assetCode.setVersion(1);
                org.getAssetCodes().add(assetCode);
                Organization newOrg = organizationRepository.save(org);
                final AssetCode savedAssetCode = newOrg.getAssetCodes().get(newOrg.getAssetCodes().size() - 1);
                Flowable<Transaction> transactions = client.getTransactionsClient().getTransactions(
                                LedgerOffset.LedgerEnd.getInstance(),
                                new FiltersByParty(
                                                Collections.singletonMap(someOperator.get().getPartyIdentifier(),
                                                                NoFilter.instance)),
                                true);
                AssetIouProcessor assetIouProcessor = new AssetIouProcessor(damlAppContext,
                                toplContext, (assetIou, assetIouContract) -> {
                                        Session session = sessionFactory.openSession();
                                        session.beginTransaction();
                                        Organization theOrg = session.find(Organization.class,
                                                        assetIou.orgId);
                                        AssetCode theCode = null;
                                        for (AssetCode code : theOrg.getAssetCodes()) {
                                                if (code.getId().equals(savedAssetCode.getId())) {
                                                        theCode = code;
                                                }
                                        }
                                        AssetCodeInventoryEntry assetCodeInventoryEntry = new AssetCodeInventoryEntry();
                                        assetCodeInventoryEntry.setBoxNonce(assetIou.boxNonce);
                                        assetCodeInventoryEntry
                                                        .setMetadata(assetIou.someMetadata.orElse("No metadata"));
                                        assetCodeInventoryEntry.setQuantity(assetIou.quantity);
                                        assetCodeInventoryEntry.setShortName(assetIou.assetCode.shortName);
                                        assetCodeInventoryEntry.setContractId(assetIouContract.contractId);
                                        theCode.getAssetCodeInventoryEntry().add(assetCodeInventoryEntry);
                                        session.save(theOrg);
                                        session.getTransaction().commit();
                                        session.close();
                                });
                transactions.forEach(assetIouProcessor::processTransaction);
                RedirectView redirectView = new RedirectView();
                redirectView.setUrl("/home/organization/" + addAssetCodeDto.getOrgName() + "/assets");
                return redirectView;
        }

}
