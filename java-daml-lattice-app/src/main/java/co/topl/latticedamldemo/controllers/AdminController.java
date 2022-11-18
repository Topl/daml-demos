package co.topl.latticedamldemo.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.daml.ledger.api.v1.CommandSubmissionServiceGrpc.CommandSubmissionServiceBlockingStub;
import com.daml.ledger.api.v1.CommandSubmissionServiceOuterClass.SubmitRequest;
import com.daml.ledger.api.v1.CommandsOuterClass.Command;
import com.daml.ledger.api.v1.CommandsOuterClass.Commands;
import com.daml.ledger.api.v1.CommandsOuterClass.CreateCommand;
import com.daml.ledger.api.v1.CommandsOuterClass.ExerciseByKeyCommand;
import com.daml.ledger.api.v1.ValueOuterClass.Identifier;
import com.daml.ledger.api.v1.ValueOuterClass.Identifier.Builder;
import com.daml.ledger.api.v1.ValueOuterClass.Record;
import com.daml.ledger.api.v1.ValueOuterClass.RecordField;
import com.daml.ledger.api.v1.ValueOuterClass.Value;
import com.daml.ledger.api.v1.admin.PartyManagementServiceGrpc.PartyManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.AllocatePartyRequest;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.AllocatePartyResponse;
import com.daml.ledger.javaapi.data.Transaction;
import com.daml.ledger.rxjava.DamlLedgerClient;

import co.topl.daml.DamlAppContext;
import co.topl.daml.ToplContext;
import co.topl.daml.operator.MembershipOfferProcessor;
import co.topl.latticedamldemo.configuration.DemoConfiguration;
import co.topl.latticedamldemo.dtos.AddMemberToOrgDto;
import co.topl.latticedamldemo.dtos.AddOrganizationDto;
import co.topl.latticedamldemo.dtos.AddUserDto;
import co.topl.latticedamldemo.model.Member;
import co.topl.latticedamldemo.model.MembersRepository;
import co.topl.latticedamldemo.model.Organization;
import co.topl.latticedamldemo.model.OrganizationRepository;
import io.reactivex.Flowable;

@Controller
@RequestMapping("/admin")
public class AdminController {

        @Autowired
        DemoConfiguration demoConfiguration;

        @Autowired
        DamlLedgerClient client;

        @Autowired
        private MembersRepository memberRepository;

        @Autowired
        private OrganizationRepository organizationRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        ToplContext toplContext;

        @Autowired
        PartyManagementServiceBlockingStub partyManagementService;

        @Autowired
        CommandSubmissionServiceBlockingStub commandSubmissionService;

        @Autowired
        Flowable<Transaction> transactions;

        @GetMapping
        public String getMainAdminPage(Model model) {
                return "admin/admin";
        }

        @GetMapping("/users")
        public String getUserAdminPage(Model model) {
                List<Member> members = new ArrayList<>();
                for (Member m : memberRepository.findAll()) {
                        members.add(m);
                }
                model.addAttribute("members", members);
                return "admin/users";
        }

        @GetMapping("/addUser")
        public String getAddUserPage(Model model) {
                model.addAttribute("addUserDto", new AddUserDto());
                return "admin/addUser";
        }

        @GetMapping("/accessDenied")
        public String getAccessDenied(Model model) {
                return "admin/accessDenied";
        }

        @PostMapping("/addUser")
        public RedirectView getAddUserSubmit(@ModelAttribute AddUserDto addUserDto, Model model) {
                AllocatePartyResponse getPartyResponse = partyManagementService
                                .allocateParty(AllocatePartyRequest.newBuilder()
                                                .setDisplayName(addUserDto.getUserName())
                                                .setPartyIdHint(addUserDto.getUserName()).build());
                String thePassword = passwordEncoder.encode(addUserDto.getPassword());
                String party = getPartyResponse.getPartyDetails().getParty();
                memberRepository.save(
                                new Member(
                                                addUserDto.getUserName(),
                                                thePassword,
                                                "USER",
                                                party));
                DamlAppContext damlAppContext = new DamlAppContext(demoConfiguration.getAppId(), party, client);
                MembershipOfferProcessor membershipOfferProcessor = new MembershipOfferProcessor(damlAppContext,
                                toplContext, (x, y) -> true, x -> true);
                transactions.forEach(membershipOfferProcessor::processTransaction);
                RedirectView redirectView = new RedirectView();
                redirectView.setUrl("/admin/users");
                return redirectView;
        }

        @GetMapping("/organizations")
        public String getOrgsAdminPage(Model model) {
                List<Organization> organizations = new ArrayList<>();
                for (Organization o : organizationRepository.findAll()) {
                        organizations.add(o);
                }
                model.addAttribute("organizations", organizations);
                return "admin/organizations";
        }

        @GetMapping("/addOrganization")
        public String getAddOrganizationPage(Model model) {
                model.addAttribute("addOrganizationDto", new AddOrganizationDto());
                return "admin/addOrganization";
        }

        @PostMapping("/addOrganization")
        public RedirectView getAddOrganizationSubmit(@ModelAttribute AddOrganizationDto addOrganizationDto,
                        Model model) {

                Builder organizationIdentifier = Identifier.newBuilder()
                                .setPackageId(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getPackageId())
                                .setEntityName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getEntityName())
                                .setModuleName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getModuleName());

                Optional<Member> someOperator = memberRepository.findById(demoConfiguration.getOperatorId());
                Organization org = new Organization(addOrganizationDto.getOrgName());
                org = organizationRepository.save(org);

                Command createCommand = Command.newBuilder().setCreate(
                                CreateCommand.newBuilder()
                                                .setTemplateId(organizationIdentifier.build())
                                                .setCreateArguments(
                                                                Record.newBuilder()
                                                                                .setRecordId(organizationIdentifier)
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("orgId")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setText(org.getId()
                                                                                                                                .toString())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("orgName")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setText(addOrganizationDto
                                                                                                                                .getOrgName())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("address")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setText(demoConfiguration
                                                                                                                                .getOperatorAddress())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("operator")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setParty(someOperator
                                                                                                                                .get()
                                                                                                                                .getPartyIdentifier())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("wouldBeMembers")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setList(com.daml.ledger.api.v1.ValueOuterClass.List
                                                                                                                                .newBuilder()
                                                                                                                                .build())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("members")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setList(com.daml.ledger.api.v1.ValueOuterClass.List
                                                                                                                                .newBuilder()
                                                                                                                                .build())))
                                                                                .addFields(RecordField.newBuilder()
                                                                                                .setLabel("assetCodesAndIous")
                                                                                                .setValue(Value.newBuilder()
                                                                                                                .setList(com.daml.ledger.api.v1.ValueOuterClass.List
                                                                                                                                .newBuilder()
                                                                                                                                .build())))
                                                                                .build()))
                                .build();

                SubmitRequest commandSubmitRequest = SubmitRequest.newBuilder()
                                .setCommands(
                                                Commands.newBuilder()
                                                                .setCommandId(UUID.randomUUID().toString())
                                                                .setParty(someOperator.get().getPartyIdentifier())
                                                                .setApplicationId(demoConfiguration.getAppId())
                                                                .addCommands(createCommand)
                                                                .build())
                                .build();

                commandSubmissionService.submit(commandSubmitRequest);

                RedirectView redirectView = new RedirectView();
                redirectView.setUrl("/admin/organizations");
                return redirectView;
        }

        @GetMapping("/addMemberToOrg")
        public String getAddMemberToOrgPage(Model model) {
                model.addAttribute("addMemberToOrgDto", new AddMemberToOrgDto());
                List<Member> members = new ArrayList<>();
                for (Member m : memberRepository.findAll()) {
                        members.add(m);
                }
                model.addAttribute("members", members);
                List<Organization> organizations = new ArrayList<>();
                for (Organization o : organizationRepository.findAll()) {
                        organizations.add(o);
                }
                model.addAttribute("organizations", organizations);
                return "admin/addMemberToOrg";
        }

        @PostMapping("/addMemberToOrg")
        public RedirectView submitAddMemberToOrgPage(@ModelAttribute AddMemberToOrgDto addMemberToOrgDto,
                        Principal principal, Model model) {

                Builder organizationIdentifier = Identifier.newBuilder()
                                .setPackageId(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getPackageId())
                                .setEntityName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getEntityName())
                                .setModuleName(co.topl.daml.api.model.topl.organization.Organization.TEMPLATE_ID
                                                .getModuleName());

                Optional<Member> someOperator = memberRepository.findById(demoConfiguration.getOperatorId());
                Optional<Member> someUser = memberRepository.findById(addMemberToOrgDto.getMember());

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
                                                                                                                                .setText(addMemberToOrgDto
                                                                                                                                                .getOrgId())))))
                                                .setChoice("Organization_InviteMember")
                                                .setChoiceArgument(
                                                                Value.newBuilder()
                                                                                .setRecord(Record
                                                                                                .newBuilder()
                                                                                                .addFields(RecordField
                                                                                                                .newBuilder()
                                                                                                                .setLabel("invitee")
                                                                                                                .setValue(Value.newBuilder()
                                                                                                                                .setParty(someUser
                                                                                                                                                .get()
                                                                                                                                                .getPartyIdentifier()))))
                                                                                .build()))
                                .build();

                SubmitRequest commandSubmitRequest = SubmitRequest.newBuilder()
                                .setCommands(
                                                Commands.newBuilder()
                                                                .setCommandId(UUID.randomUUID().toString())
                                                                .setParty(someOperator.get().getPartyIdentifier())
                                                                .setApplicationId(demoConfiguration.getAppId())
                                                                .addCommands(exerciseCommand)
                                                                .build())
                                .build();

                commandSubmissionService.submit(commandSubmitRequest);

                RedirectView redirectView = new RedirectView();
                redirectView.setUrl("/admin/organizations");
                return redirectView;
        }

}
