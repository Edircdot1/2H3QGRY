package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BranchType
import com.example.model.UserRole
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ManagementDashboardScreen(
  onOpenBayManagement: () -> Unit = {}
) {
  val branches by AppRepository.branches.collectAsState()
  val franchiseContracts by AppRepository.franchiseContracts.collectAsState()
  val explanationRequests by AppRepository.explanationRequests.collectAsState()
  val auditLogs by AppRepository.auditLogs.collectAsState()
  val currentUser by AppRepository.currentUser.collectAsState()
  val sdf = remember { SimpleDateFormat("HH:mm:ss dd/MM", Locale("vi", "VN")) }

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Tổng quan mạng lưới, 1: Hợp đồng nhượng quyền, 2: Giải trình hợp đồng, 3: Nhật ký Audit

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SystemBg),
    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Management Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "TRUNG TÂM ĐIỀU HÀNH & GIÁM SÁT",
            style = MaterialTheme.typography.labelSmall.copy(
              color = LabelTertiary,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "Hệ thống iVehicle VN",
            style = MaterialTheme.typography.headlineLarge.copy(color = LabelPrimary)
          )
        }

        StatusPill(
          text = currentUser.role.label,
          tintColor = PrimaryBlue
        )
      }
    }

    // Role switcher selector bar for testing and demonstration
    item {
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(12.dp)
      ) {
        Text(
          text = "CHUYỂN ĐỔI GÓC NHÌN NGHIỆP VỤ (DEMO RBAC):",
          style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          UserRole.entries.forEach { role ->
            val isCurrentRole = currentUser.role == role
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isCurrentRole) PrimaryBlue else Color(0xFFE2E2E7))
                .clickable { AppRepository.switchUserRole(role) }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = when (role) {
                  UserRole.CUSTOMER -> "Khách"
                  UserRole.TECHNICIAN -> "KTV"
                  UserRole.BRANCH_MANAGER -> "Quản lý"
                  UserRole.HQ_ADMIN -> "Trụ sở"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentRole) Color.White else LabelPrimary
              )
            }
          }
        }
      }
    }

    // Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf("Chi nhánh & Tải", "Nhượng quyền", "Giải trình (${explanationRequests.size})", "Audit Log").forEachIndexed { index, title ->
          val isSelected = selectedTab == index
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) SecondaryDark else Color.Transparent)
              .clickable { selectedTab = index }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else LabelSecondary
              ),
              maxLines = 1
            )
          }
        }
      }
    }

    when (selectedTab) {
      0 -> {
        // Manager Live Bay Monitor Trigger Card
        item {
          BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PositivusGreen,
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(16.dp),
            onClick = onOpenBayManagement
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PositivusDark),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Filled.Garage, contentDescription = null, tint = PositivusGreen, modifier = Modifier.size(24.dp))
                }
                Column {
                  Text("ĐIỀU HÀNH KHOANG SỬA CHỮA (LIVE)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = PositivusDark)
                  Text("Giám sát 5 khoang & Điều phối KTV", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PositivusDark)
                }
              }

              Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = PositivusDark)
            }
          }
        }

        // High level KPI Cards
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            GlassCard(
              modifier = Modifier.weight(1f),
              cornerRadius = 18.dp,
              contentPadding = PaddingValues(14.dp)
            ) {
              Text("TỔNG CHI NHÁNH", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
              Text("${branches.size}", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
              Text("3 Trực thuộc • 1 Nhượng quyền", style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary))
            }

            GlassCard(
              modifier = Modifier.weight(1f),
              cornerRadius = 18.dp,
              contentPadding = PaddingValues(14.dp)
            ) {
              Text("CÔNG SUẤT BÌNH QUÂN", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
              val avgLoad = (branches.sumOf { it.currentActiveJobs }.toDouble() / branches.sumOf { it.maxDailyCapacity } * 100).toInt()
              Text("$avgLoad%", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = TertiaryGreen))
              Text("37/100 khoang sửa chữa", style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary))
            }
          }
        }

        // Branch List with operational load
        items(branches) { branch ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  StatusPill(
                    text = branch.type.label,
                    tintColor = if (branch.type == BranchType.DIRECT) PrimaryBlue else Color(0xFFE65100)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "${branch.address}, ${branch.district}, ${branch.city}",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
                Text(
                  text = "Hotline: ${branch.phone}",
                  style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GlassSeparator, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Capacity bar
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Tải khoang tiếp nhận:",
                  style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary)
                )
                Text(
                  text = "${branch.currentActiveJobs}/${branch.maxDailyCapacity} xe đang xử lý",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { branch.currentActiveJobs.toFloat() / branch.maxDailyCapacity },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = if (branch.currentActiveJobs > branch.maxDailyCapacity * 0.8) ErrorRed else PrimaryBlue,
                trackColor = Color(0xFFE2E2E7)
              )
            }
          }
        }
      }

      1 -> {
        // Franchise Contracts & Financial Disclosures
        items(franchiseContracts) { contract ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 22.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column {
                Text(
                  text = "HỢP ĐỒNG #${contract.id}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = contract.branchName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Chủ đầu tư: ${contract.franchiseeOwner}",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
              }

              StatusPill(
                text = "Tuân thủ: ${contract.complianceScore}%",
                tintColor = TertiaryGreen
              )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GlassSeparator, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("VỐN ĐẦU TƯ BAN ĐẦU", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
                Text(AppRepository.formatVnd(contract.investmentCapitalVnd), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
              }
              Column {
                Text("PHÍ NHƯỢNG QUYỀN", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
                Text("${contract.monthlyRoyaltyPercent}% Doanh thu/tháng", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("DOANH THU THỰC TẾ THÁNG", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
                Text(AppRepository.formatVnd(contract.currentRevenueVnd), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TertiaryGreen))
              }
              Column {
                Text("CHI PHÍ VẬN HÀNH THỰC TẾ", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
                Text(AppRepository.formatVnd(contract.currentExpenseVnd), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
              }
            }

            if (contract.pendingAuditIssues > 0) {
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFFFF3E0))
                  .padding(10.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFE65100))
                  Text(
                    text = "Quy trình kiểm soát: Có 1 chênh lệch cần tạo yêu cầu giải trình theo hợp đồng nhượng quyền.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE65100))
                  )
                }
              }
            }
          }
        }
      }

      2 -> {
        // Franchise Contract Explanation Requests
        item {
          Text(
            text = "QUY TRÌNH XỬ LÝ SAI LỆCH HỢP ĐỒNG (YÊU CẦU GIẢI TRÌNH)",
            style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
          )
        }

        items(explanationRequests) { req ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "MÃ YÊU CẦU: ${req.id}",
                  style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = req.branchName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = req.contractClause,
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
              }

              StatusPill(
                text = req.status.label,
                tintColor = if (req.status == com.example.model.ExplanationStatus.RESOLVED) TertiaryGreen else Color(0xFFE65100)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = GlassSeparator, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Vấn đề phát hiện:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = req.discrepancyDetail,
              style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Hạn chót giải trình: ${req.deadlineDate}",
                style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary)
              )
            }

            if (req.responseNote.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFE8F5E9))
                  .padding(10.dp)
              ) {
                Text(
                  text = "✓ Giải trình đại lý: ${req.responseNote}",
                  style = MaterialTheme.typography.bodySmall.copy(color = TertiaryGreen, fontWeight = FontWeight.Bold)
                )
              }
            } else {
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  AppRepository.submitExplanationResponse(
                    req.id,
                    "3 can dầu đã xuất dùng cho xe nội bộ xưởng test động cơ, đã bổ sung chứng từ nội bộ."
                  )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Gửi Giải Trình & Biên Bản Bổ Sung", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      3 -> {
        // System Audit Trail
        item {
          Text(
            text = "NHẬT KÝ KIỂM TOÁN HỆ THỐNG (AUDIT TRAIL BẮT BUỘC)",
            style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
          )
        }

        items(auditLogs) { log ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            contentPadding = PaddingValues(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "[${log.action}]",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = PrimaryBlue
                    )
                  )
                  Text(
                    text = "• ${log.targetEntity}",
                    style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary)
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = log.details,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                  text = "Thực hiện bởi: ${log.actorName} (${log.actorRole.label})",
                  style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
                )
              }

              Text(
                text = sdf.format(Date(log.timestampMillis)),
                style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(70.dp))
    }
  }
}
